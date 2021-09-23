package com.yu.common.common.util;

import com.baomidou.mybatisplus.core.toolkit.EncryptUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorUtil {
    private static final AtomicInteger orderIdCount = new AtomicInteger(0);
    private static final AtomicInteger orderIdCount2 = new AtomicInteger(0);


    private static final SimpleDateFormat CONST_order_id_format = new SimpleDateFormat("yyyyMMHHmmss");

    public static String genVerifyCode() {
        String time = System.currentTimeMillis() + "";
        return time.substring(time.length() - 4);
    }

    public static String genSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    // 生成订单号
    public static String generateOrderNo() {
        int i = orderIdCount.incrementAndGet() % 1000 + 1000;
        return CONST_order_id_format.format(new Date()) + "-" + i; // 1台机器1秒最大1千个订单
    }

    // 生成退款号
    public static String generateRefundNo() {
        int i = orderIdCount2.incrementAndGet() % 1000 + 1000;
        return "refund-" + CONST_order_id_format.format(new Date()) + "-" + i; // 1台机器1秒最大1千个退款号
    }

    public static String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * md5密码加密
     */
    public static String md5Base64(String password) {
        return EncryptUtils.md5Base64(password);
    }

    /**
     * 生成过期时间
     * @param ttl 多少秒后过期
     * @return 过期时间的时间戳，ms
     */
    public static long generateExpireTime(long ttl) {
        return System.currentTimeMillis() + ttl * 1000;
    }

//    public static void main(String[] args) {
//
//        System.out.println(md5Base64("123456"));
//    }
}

