package com.yu.common.common.enums;

// 商品属性的种类
public enum GoodsPropertyCategory {

    ENUM_size("大小"),
    ENUM_temperature("温度"),
    ENUM_tian_du("甜度"),
    ENUM_kou_wei("口味"),
    ENUM_jia_liao("加料");

    public String value;

    GoodsPropertyCategory(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
