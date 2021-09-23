package com.yu.system.modules.app.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.common.constant.Const;
import com.yu.common.common.enums.OrderStatus;
import com.yu.common.common.enums.OrderTakeType;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.PayjsUtil;
import com.yu.common.common.util.http.HttpUtil;
import com.yu.common.entity.app.OrderInfo;
import com.yu.system.modules.app.mapper.OrderInfoAdminMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OrderInfoAdminService {

    @Resource
    private OrderInfoAdminMapper orderInfoAdminMapper;

    /**
     * 分页条件查询
     *
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return Page<OrderInfoAdmin>
     */
    public Page<OrderInfo> getOrderInfoAdminByPage(int pageNo, int pageSize, String orderStatus) {
        Page<OrderInfo> page = new Page<>(pageNo, pageSize);
        if (StringUtils.isEmpty(orderStatus)) {
            return orderInfoAdminMapper.selectPage(page, new QueryWrapper<OrderInfo>().orderByDesc("create_time"));
        } else {
            return orderInfoAdminMapper.selectPage(page, new QueryWrapper<OrderInfo>()
                    .eq("order_status", orderStatus).orderByDesc("create_time"));
        }
    }


    // 进入订单的下一个步
    @Transactional(rollbackFor = Exception.class)
    public String toNextOrderStatus(String orderNo, String currentStatus) throws ServiceException {
        OrderInfo orderInfo = orderInfoAdminMapper.selectById(orderNo);
        if (OrderStatus.ENUM_on_making.value.equalsIgnoreCase(orderInfo.getOrderStatus())) { // 制作中
            if (OrderTakeType.ENUM_take_out.value.equals(orderInfo.getTakeType())) { // 外卖配送方式
                orderInfo.setOrderStatus(OrderStatus.ENUM_on_sending.value); // 修改为配送中
            } else {
                orderInfo.setOrderStatus(OrderStatus.ENUM_please_take_meal.value); // 修改为请取餐
            }
            orderInfo.setFinishTime(new Date()); // 完成时间
            // TODO 通过公众号消息通知用户取餐
            try {
//                WeixinUtil.sendMpMessageOfFinishOrder(orderInfo.getWxOpenid(), orderInfo.getOrderNo(), orderInfo.getGoodsPreview());
            } catch (Exception e) {
                log.error("通过公众号消息通知用户取餐，通知失败");
            }
        } else if (OrderStatus.ENUM_on_sending.value.equalsIgnoreCase(orderInfo.getOrderStatus())) { // 配送中
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_received.value); // 修改为已送达
        } else if (OrderStatus.ENUM_please_take_meal.value.equalsIgnoreCase(orderInfo.getOrderStatus())) { // 请取餐
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_received.value); // 修改为已送达
        } else if (OrderStatus.ENUM_has_received.value.equalsIgnoreCase(orderInfo.getOrderStatus())) { // 已送达
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_completed.value); // 完成订单
            orderInfo.setFinishTime(new Date());
        } else {
            throw ServiceException.CONST_current_order_status_can_not_change;
        }
        orderInfoAdminMapper.updateById(orderInfo);
        return orderInfo.getOrderStatus();
    }

    public String cancelOrderNotPay(String orderNo) throws ServiceException {
        OrderInfo orderInfoAdmin = orderInfoAdminMapper.selectById(orderNo);
        if (orderInfoAdmin == null)
            throw ServiceException.CONST_order_not_exist;

        if (OrderStatus.ENUM_has_not_pay_money.value.equals(orderInfoAdmin.getOrderStatus())) {
            orderInfoAdmin.setOrderStatus(OrderStatus.ENUM_has_canceled.value);
            orderInfoAdmin.setExtraInfo("订单取消原因: [商家取消]");
            orderInfoAdminMapper.updateById(orderInfoAdmin);
            return "取消成功";
        }
        return "无法取消, 用户已完成了支付";
    }

    // 商家取消订单执行退款
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAndRefund(String orderNo, String reason) throws ServiceException {
        OrderInfo orderInfo = orderInfoAdminMapper.selectById(orderNo);
        if (orderInfo == null)
            throw ServiceException.CONST_order_not_exist;

        Map<String, String> payData = new HashMap<>();
        payData.put("mchid", Const.PayJS.mchid);
        payData.put("payjs_order_id", orderInfo.getPayjsOrderId()); // payjs订单号
        payData.put("sign", PayjsUtil.sign(payData));
        String result = HttpUtil.post(Const.PayJS.refundUrl, JSON.toJSONString(payData));
        if (JSON.parseObject(result).getIntValue("return_code") == 1) { // 退款成功
            orderInfo.setOrderStatus(OrderStatus.ENUM_has_canceled.value);
            orderInfo.setExtraInfo("【取消原因: " + reason + "】");
            return orderInfoAdminMapper.updateById(orderInfo) == 1;
        }
        return false;
    }
}
