package com.yu.common.service;

import com.yu.common.entity.common.RecentOrderMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

import static com.yu.common.common.constant.Const.CONST_recent_order_message_map;

// 最新订单消息
@Service
public class OrderMessageService {

    @Resource
    private RedisService redisService;

    // 查询最新的订单消息
    public Collection<Object> getResentOrderMessages() {
        return redisService.hmget(CONST_recent_order_message_map).values();
    }

    // 确认收到消息
    public String confirmReceiveOrderMessage(String orderNo) {
        Object orderMessage = redisService.hget(CONST_recent_order_message_map, orderNo);
        if (orderMessage == null)
            return "已经确认过了";

        redisService.hdel(CONST_recent_order_message_map, orderNo);
        return "已确认收到";
    }

    // 添加最新订单消息
    public boolean addOrderMessage(RecentOrderMessage message) {
        return redisService.hset(CONST_recent_order_message_map, message.getOrderNo(), message);
    }
}
