package com.yu.app.moudles.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.app.moudles.mapper.OrderMapper;
import com.yu.common.common.constant.Const;
import com.yu.common.common.enums.OrderStatus;
import com.yu.common.common.enums.OrderTakeType;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.common.util.PayjsUtil;
import com.yu.common.common.util.http.HttpUtil;
import com.yu.common.common.util.result.Result;
import com.yu.common.entity.app.OrderInfo;
import com.yu.common.entity.app.form.CreateOrderForm;
import com.yu.common.entity.app.vo.HistoryOrderVO;
import com.yu.common.entity.common.AppConfig;
import com.yu.common.entity.common.RecentOrderMessage;
import com.yu.common.service.AppConfigService;
import com.yu.common.service.LockService;
import com.yu.common.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderMessageService orderMessageService;
    @Resource
    private AppConfigService appConfigService;
    @Resource
    private LockService lockService;

    @Transactional(rollbackFor = Exception.class)
    public Result<String> createOrder(CreateOrderForm orderParams, String wxOpenid) {
        if (orderParams.getTotalPrice() < 0)
            return Result.fail("订单的价格异常");
        if (orderParams.getGoodsTotalNum() <= 0)
            return Result.fail("不能生成空订单，请选择商品");
        // 外卖配送 检验参数
        if (OrderTakeType.ENUM_take_out.value.equals(orderParams.getTakeType()))
            if (StringUtils.isEmpty(orderParams.getAddressDetail())
                    || orderParams.getAddressDetail().trim().length() < 3 // 尽量确保收货地址等正确性
                    || StringUtils.isEmpty(orderParams.getUserPhone())
                    || StringUtils.isEmpty(orderParams.getReceiver()))
                return Result.fail("收货信息错误, 请检查收获地址、手机和姓名");

        AppConfig appConfig = appConfigService.getAppConfig();
        if (!appConfig.getShopStatus()) {
            return Result.fail("商家休息中，下单失败");
        }
        Date currentTime = new Date();
        int nowHour = LocalTime.now(ZoneId.of("Asia/Shanghai")).getHour();
        if (!(nowHour >= appConfig.getBusinessStartTime() && nowHour < appConfig.getBusinessEndTime()))
            return Result.fail("未到营业时间，下单失败");

        if (!lockService.tryLock(Const.CONST_lock_redis_prefix + wxOpenid, "", 15))
            return Result.fail("正在下单中，请勿重复下单");

        try {
            // TODO 数据库没有订单商品表，将订单商品的信息全部以字符串(奶茶1[加冰 中杯 少量糖 ]*1)这样的方式存在订单表里，价格也是让小程序里去算
            OrderInfo orderInfo = new OrderInfo();
            BeanUtils.copyProperties(orderParams, orderInfo);
            orderInfo.setWxOpenid(wxOpenid);
            orderInfo.setOrderNo(GeneratorUtil.generateOrderNo());
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_not_pay_money.value);
            orderInfo.setCreateTime(currentTime);
            orderInfo.setGoodsPreview(orderParams.getGoodsPreview());
            orderInfo.setGoodsTotalNum(orderParams.getGoodsTotalNum());
            orderInfo.setTotalPrice(orderParams.getTotalPrice());
            orderInfo.setPayPrice(null); // 实际支付价格用支付成功异步回调去写
            orderMapper.insert(orderInfo);

            // TODO 通知订单消息, 可以使用redis或者rabbitMQ
            RecentOrderMessage message = new RecentOrderMessage();
            message.setOrderNo(orderInfo.getOrderNo());
            message.setAddress(orderInfo.getAddressDetail());
            orderMessageService.addOrderMessage(message);
            return Result.ok(orderInfo.getOrderNo());
        } finally {
            lockService.releaseLock(Const.CONST_lock_redis_prefix + wxOpenid);
        }
    }

    public Page<HistoryOrderVO> getHistoryOrderByPage(Integer pageNo, Integer pageSize, String wxOpenid) {
        Page<HistoryOrderVO> page = new Page<>(pageNo, pageSize);
        page.setRecords(orderMapper.getHistoryOrderByPage(wxOpenid, (pageNo - 1) * pageSize, pageSize));
        page.setTotal(orderMapper.getHistoryOrderTotalCount(wxOpenid));
        return page;
    }

    public OrderInfo getOrderDetail(String orderNo) throws ServiceException {
        return orderMapper.selectById(orderNo);
    }

    // 取消订单，用户在未支付的情况下才能取消订单
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cancelOrder(String orderNo) {
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        if (OrderStatus.ENUM_has_not_pay_money.value.equalsIgnoreCase(orderInfo.getOrderStatus())) {
            if (!StringUtils.isEmpty(orderInfo.getPayjsOrderId())) {
                Map<String, String> payData = new HashMap<>();
                payData.put("mchid", Const.PayJS.mchid);
                payData.put("payjs_order_id", orderInfo.getPayjsOrderId()); // payjs订单号
                payData.put("sign", PayjsUtil.sign(payData));
                HttpUtil.post(Const.PayJS.closeUrl, JSON.toJSONString(payData)); // 不用完全保证微信那边的订单也取消了，2小时自动取消
            }
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_canceled.value);
            orderMapper.updateById(orderInfo);
            return Result.ok("订单已取消");
        }
        return Result.fail("未付款的订单才能取消");
    }

    // 用户确认收货
    @Transactional(rollbackFor = Exception.class)
    public Integer finishedOrder(String orderNo, String wxOpenid) throws ServiceException {
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        // '请取餐' 或 '已送达' 时用户才能让去确认收货
        if (OrderStatus.ENUM_please_take_meal.value.equalsIgnoreCase(orderInfo.getOrderStatus())
                || OrderStatus.ENUM_has_received.value.equalsIgnoreCase(orderInfo.getOrderStatus()))
            return orderMapper.finishOrder(orderNo, OrderStatus.ENUM_has_completed.value, new Date());

        throw ServiceException.CONST_confirm_receive_failed;
    }

    // 获取正在处理的订单
    public List<OrderInfo> getHandlingOrders(String wxOpenid) throws ServiceException {
        List<String> params = new ArrayList<>();
        params.add(OrderStatus.ENUM_has_completed.value);
        params.add(OrderStatus.ENUM_has_canceled.value);
        params.add(OrderStatus.ENUM_has_refunded.value);
        params.add(OrderStatus.ENUM_on_refunding.value);

        return orderMapper.selectList(new QueryWrapper<OrderInfo>()
                .eq("wx_openid", wxOpenid)
                .notIn("order_status", params)
                .orderByDesc("create_time"));
    }
}
