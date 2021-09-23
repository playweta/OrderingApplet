package com.yu.app.moudles.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.app.common.util.session.SessionUtil;
import com.yu.app.moudles.mapper.OrderMapper;
import com.yu.app.moudles.service.OrderServiceImpl;
import com.yu.common.common.annotation.AccessLimiter;
import com.yu.common.common.annotation.NeedLogin;
import com.yu.common.common.constant.Const;
import com.yu.common.common.enums.OrderStatus;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.common.util.PayjsUtil;
import com.yu.common.common.util.http.HttpUtil;
import com.yu.common.common.util.result.Result;
import com.yu.common.entity.app.OrderInfo;
import com.yu.common.entity.app.form.CreateOrderForm;
import com.yu.common.entity.app.vo.HistoryOrderVO;
import com.yu.common.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(tags = "订单服务")
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderServiceImpl orderService;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private RedisService redisService;

    @ApiModelProperty("下订单")
    @NeedLogin
    @PostMapping
    public Result<String> createOrder(@RequestBody CreateOrderForm orderParams, HttpServletRequest request) throws ServiceException {
        return orderService.createOrder(orderParams, SessionUtil.getCurrentWxOpenidFromRequest(request));
    }

    @ApiOperation("获取payJS支付小程序支付需要的支付参数信息")
    @NeedLogin
    @AccessLimiter
    @GetMapping("/payMP/{orderNo}")
    public Result payJSMP(@PathVariable String orderNo, HttpServletRequest request) throws ServiceException {
        String openid = SessionUtil.getCurrentWxOpenidFromRequest(request);
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        if (orderInfo == null || !orderInfo.getWxOpenid().equals(openid))
            return Result.fail("订单不存在");

        Map<String, String> payData = new HashMap<>();
        payData.put("mchid", Const.PayJS.mchid);
        payData.put("total_fee", "" + orderInfo.getTotalPrice());
        payData.put("out_trade_no", orderInfo.getOrderNo());
        String title = orderInfo.getGoodsPreview();
        if (title.length() > 60)
            title = title.substring(0, title.length() - 5) + "...";
        payData.put("body", title);
        payData.put("notify_url", Const.PayJS.notifyUrl);
        payData.put("nonce", UUID.randomUUID().toString());
        payData.put("sign", PayjsUtil.sign(payData));
        return Result.ok(payData);
    }

    // h5收银台方式
    @NeedLogin
    @GetMapping("/payCashier/{orderNo}")
    public Result<String> cashier(@PathVariable String orderNo, HttpServletRequest request) throws ServiceException, UnsupportedEncodingException {
        String openid = SessionUtil.getCurrentWxOpenidFromRequest(request);
        OrderInfo orderInfo = orderMapper.selectById(orderNo);
        if (orderInfo == null || !orderInfo.getWxOpenid().equals(openid))
            return Result.fail("订单不存在");
        Map<String, String> payData = new HashMap<>();
        payData.put("mchid", Const.PayJS.mchid);
        payData.put("total_fee", "" + orderInfo.getTotalPrice()); // 金额
        payData.put("out_trade_no", orderNo); // 自己系统的订单号
        payData.put("body", orderNo); // 这里不要弄中文，除非url编码
        payData.put("notify_url", Const.PayJS.notifyUrl);
        payData.put("callback_url", "https://xxl.today/naicha_ui");
        payData.put("auto", "1");
        payData.put("hide", "1");
        payData.put("sign", PayjsUtil.sign(payData));
        return Result.ok(Const.PayJS.cashierUrl + "?" + HttpUtil.toQueryStr(payData));
    }

    // TODO 小程序支付成功后马上修改订单的标志
    @ApiOperation("完成支付，并设置payjs的orderid")
    @NeedLogin
    @PutMapping("/finishWeixinPay")
    public Result finishWeixinPay(@RequestBody Map<String, String> data) {
        OrderInfo orderInfo = orderMapper.selectById(data.get("outTradeNo"));
        orderInfo.setPayjsOrderId(data.get("payjsOrderId"));
        if (data.get("success").equals("true"))
            orderInfo.setOrderStatus(OrderStatus.ENUM_on_making.value);
        return Result.ok(orderMapper.updateById(orderInfo));
    }

    @ApiOperation(value = "接收支付返回的消息")
    @AccessLimiter(seconds = 5, maxCount = 30)
    @PostMapping(value = "/pay/notify")
    @Transactional
    public Result parseOrderNotifyResult(HttpServletRequest req, HttpServletResponse response) {
        Map<String, String> notifyData = new HashMap<>();
        notifyData.put("return_code", req.getParameter("return_code"));
        notifyData.put("total_fee", req.getParameter("total_fee"));
        notifyData.put("out_trade_no", req.getParameter("out_trade_no")); // 自己系统里的订单号
        notifyData.put("payjs_order_id", req.getParameter("payjs_order_id"));
        notifyData.put("transaction_id", req.getParameter("transaction_id")); // 微信用户手机显示订单号
        notifyData.put("time_end", req.getParameter("time_end"));
        notifyData.put("openid", req.getParameter("openid"));
        notifyData.put("mchid", req.getParameter("mchid"));
        if (!StringUtils.isEmpty(req.getParameter("attach")))
            notifyData.put("attach", req.getParameter("attach")); // 用户自定义的数据原样返回
        if (!StringUtils.isEmpty(req.getParameter("type")))
            notifyData.put("type", req.getParameter("type"));  // 支付宝交易传值：alipay ，微信支付无需此字段
        if (PayjsUtil.sign(notifyData).equals(req.getParameter("sign"))) {
            String orderNo = req.getParameter(("out_trade_no"));
            if (!"1".equals(req.getParameter(("return_code")))) {
                response.setStatus(HttpStatus.HTTP_BAD_REQUEST);
                return Result.fail("订单" + orderNo + "处理失败, 未支付!");
            }

            // TODO 这里的回调会在小程序支付成功几秒后左右，插入微信交易号和支付金额
            // 官方: 自己处理订单的业务逻辑，需要判断订单是否已经支付过，否则可能会重复调用,
            OrderInfo info = new OrderInfo();
            info.setOrderNo(orderNo);
            info.setOrderStatus(OrderStatus.ENUM_on_making.value);
            info.setPayjsOrderId(req.getParameter("payjs_order_id"));
            info.setWxPayTransactionId(req.getParameter("transaction_id"));
            info.setPayPrice(Integer.parseInt(req.getParameter("total_fee")));
            try {
                info.setPayTime(DateUtil.parseTime(req.getParameter("time_end")));
            } catch (Exception e) {
                log.warn("fail to parse time_end");
            }
            orderMapper.updateById(info);
            return Result.ok();
        }
        response.setStatus(HttpStatus.HTTP_BAD_REQUEST);
        return Result.fail("验签失败");
    }

    @ApiOperation("取消订单")
    @NeedLogin
    @DeleteMapping("/cancel/{orderNo}")
    public Result<String> cancelOrder(@ApiParam("订单号") @PathVariable String orderNo) {
        return orderService.cancelOrder(orderNo);
    }

    @ApiOperation("确认收货")
    @NeedLogin
    @PutMapping("/confirmReceive/{orderNo}")
    public Result confirm(@ApiParam("订单号") @PathVariable String orderNo, HttpServletRequest request) throws ServiceException {
        return Result.ok(orderService.finishedOrder(orderNo, SessionUtil.getCurrentWxOpenidFromRequest(request)));
    }

    @ApiOperation("分页获取历史订单")
    @NeedLogin
    @GetMapping("/history/page/{pageNo}/{pageSize}")
    public Result<Page<HistoryOrderVO>> getHistoryOrderByPage(@PathVariable Integer pageNo,
                                                              @PathVariable Integer pageSize,
                                                              HttpServletRequest request) throws ServiceException {
        return Result.ok(orderService.getHistoryOrderByPage(pageNo, pageSize, SessionUtil.getCurrentWxOpenidFromRequest(request)));
    }

    @ApiOperation("订单详情信息")
    @GetMapping("/detail/{orderNo}")
    public Result<OrderInfo> getOrderDetail(@PathVariable String orderNo) throws ServiceException {
        return Result.ok(orderService.getOrderDetail(orderNo));
    }

    @ApiOperation("获取正在处理的订单列表")
    @NeedLogin
    @AccessLimiter
    @GetMapping("/notCompleted")
    public Result<List<OrderInfo>> getHandlingOrders(HttpServletRequest request) throws ServiceException {
        return Result.ok(orderService.getHandlingOrders(SessionUtil.getCurrentWxOpenidFromRequest(request)));
    }

}
