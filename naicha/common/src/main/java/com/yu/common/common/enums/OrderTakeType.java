package com.yu.common.common.enums;

// 订单的取餐方式
public enum OrderTakeType {

    ENUM_take_in("到店自取"),
    ENUM_take_out("外卖配送");

    public String value;

    OrderTakeType( String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    }
