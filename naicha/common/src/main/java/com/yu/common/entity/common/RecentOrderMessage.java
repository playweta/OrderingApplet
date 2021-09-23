package com.yu.common.entity.common;

import lombok.Data;

// 最新的订单消息
@Data
public class RecentOrderMessage {
    private String orderNo;
    private String address;
}
