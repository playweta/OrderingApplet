package com.yu.system.modules.app.controller;

import com.yu.common.common.annotation.AccessLimiter;
import com.yu.common.common.util.result.Result;
import com.yu.common.entity.common.RecentOrderMessage;
import com.yu.common.service.OrderMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "系统：消息服务")
@RestController
@RequestMapping("/message")
public class OrderMessageController {

    @Resource
    private OrderMessageService orderMessageService;

    @ApiOperation("查询最新的订单消息")
    @AccessLimiter
    @GetMapping("/resentOrderMessage")
    public Result getResentOrderMessages() {
        return Result.ok(orderMessageService.getResentOrderMessages());
    }

    @ApiOperation("确认收到消息")
    @DeleteMapping("/confirmReceiveOrderMessage/{orderNo}")
    public Result<String> confirmReceiveOrderMessage(@PathVariable String orderNo) {
        return Result.ok(orderMessageService.confirmReceiveOrderMessage(orderNo));
    }

}
