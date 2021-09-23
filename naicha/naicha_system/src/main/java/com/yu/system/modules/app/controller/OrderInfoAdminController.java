package com.yu.system.modules.app.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.common.constant.Const;
import com.yu.common.common.enums.OrderStatus;
import com.yu.common.common.util.PayjsUtil;
import com.yu.common.common.util.http.HttpUtil;
import com.yu.common.entity.app.OrderInfo;
import com.yu.system.modules.app.mapper.OrderInfoAdminMapper;
import com.yu.system.modules.app.service.OrderInfoAdminService;
import com.yu.common.common.annotation.AccessLimiter;
import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "系统：订单管理")
@RestController
@RequestMapping("/orderInfoAdmin")
public class OrderInfoAdminController {
    @Resource
    private OrderInfoAdminMapper orderInfoAdminMapper;

    @Resource
    private OrderInfoAdminService orderInfoAdminService;


    @ApiOperation("分页查询")
    @NeedPermission("system:app:orderInfo:list")
    @GetMapping("/page")
    public Result<Page<OrderInfo>> getOrderInfoAdminByPage(@RequestParam(defaultValue = "1") int pageNo,
                                                           @RequestParam(defaultValue = "10") int pageSize,
                                                           @RequestParam(required = false) String orderStatus) {
        return Result.ok(orderInfoAdminService.getOrderInfoAdminByPage(pageNo, pageSize, orderStatus));
    }


    @ApiOperation("进入订单的下一个步")
    @NeedPermission("system:app:orderInfo:update")
    @PostMapping("/nextStatus/{orderNo}/{currentStatus}")
    public Result<String> toNextOrderStatus(@PathVariable String orderNo, @PathVariable String currentStatus) throws ServiceException {
        return Result.ok(orderInfoAdminService.toNextOrderStatus(orderNo, currentStatus));
    }

    @ApiOperation("主动查询未付款订单的支付状态")
    @NeedPermission("system:app:orderInfo:status")
    @AccessLimiter
    @GetMapping("/wxPayStatus/{orderNo}")
    public Result<String> queryWeixinOrder(@PathVariable String orderNo) {
        OrderInfo info = orderInfoAdminMapper.selectById(orderNo);
        if (info == null)
            return Result.fail("订单不存在");
        if (!info.getOrderStatus().equals(OrderStatus.ENUM_has_not_pay_money.value))
            return Result.ok(info.getOrderStatus());

        if (StringUtils.isEmpty(info.getPayjsOrderId()))
            return Result.fail("未支付");

        Map<String, String> payData = new HashMap<>();
        payData.put("mchid", Const.PayJS.mchid);
        payData.put("payjs_order_id", info.getPayjsOrderId()); // payjs订单号
        payData.put("sign", PayjsUtil.sign(payData));
        String result = HttpUtil.post(Const.PayJS.checkUrl, JSON.toJSONString(payData));
        JSONObject res = JSON.parseObject(result);
        if (res.getIntValue("return_code") == 0)
            return Result.fail("订单查询失败");

        if (res.getIntValue("status") == 1) {
            info.setWxPayTransactionId(res.getString(""));
            info.setPayTime(DateUtil.parseTime(res.getString("paid_time")));
            info.setPayPrice(res.getIntValue("total_fee"));
            orderInfoAdminMapper.updateById(info);
            return Result.ok("已支付");
        }
        return Result.fail("未支付");
    }

    @ApiOperation("商家取消没有付款的订单")
    @NeedPermission("system:app:orderInfo:cancel")
    @PutMapping("/cancelOrderNotPay/{orderNo}")
    public Result cancelOrderNotPay(@PathVariable String orderNo) throws ServiceException {
        return Result.ok(orderInfoAdminService.cancelOrderNotPay(orderNo));
    }

    @ApiOperation("商家取消订单并执行退款")
    @NeedPermission("system:app:orderInfo:refund")
    @PutMapping("/cancelAndRefund/{orderNo}")
    public Result cancelAndRefund(@PathVariable String orderNo, String reason) throws ServiceException {
        return Result.ok(orderInfoAdminService.cancelAndRefund(orderNo, reason));
    }
}
