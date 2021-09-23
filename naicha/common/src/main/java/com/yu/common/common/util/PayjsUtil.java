package com.yu.common.common.util;


import com.yu.common.common.constant.Const;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class PayjsUtil {
    // 签名算法
    public static String sign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        TreeSet<String> sortSet = new TreeSet<>(params.keySet());
        for (String key : sortSet)
            sb.append(key).append("=").append(params.get(key)).append("&");
        sb.append("key=").append(Const.PayJS.key);
        return MD5Util.md5Hex(sb.toString()).toUpperCase();
    }
}
