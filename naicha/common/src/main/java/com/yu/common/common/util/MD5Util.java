package com.yu.common.common.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MD5Util {


    // 生成随机盐
    public static String randSalt() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // md5加密
    public static String md5Hex(String str) {
        return md5Hex(str, "");
    }

    // md5盐值加密, 返回eg:c393b13019662a34d3414f634b9c72fa
    public static String md5Hex(String str, String salt) {
        return DigestUtils.md5Hex((str + salt).getBytes(StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        String password = "123";
        String salt = "abcabcabcabcabc";
        System.out.println(md5Hex(password, salt));
    }
}