package com.yu.common.common.constant;

public final class Const {
    // 微信
    public static class Wx {
        // 陈亚如
        public static final String appId = "wxe63b0775e6b534e6";
        public static final String appSecret = "6af3b72441fc38f6cb8a201b15d85c56";
        public static final String chenyaruWxOpenid = "o7LFAwavj-o4wKkEXdpA-kQmmajc"; // 陈亚如微信, 测试用
    }

    // 阿里云短信服务
    public static class AliyunSms {
        public static final String accessKeyId = "LTAI5tFPT8ZpESQKyHqxxKku";
        public static final String accessKeySecret = "8YX9j9n5etsxg1K8EOzSFdG6JCUc2Q";
    }

    // 腾讯云短信服务
    public static class TxyunSms {
        public static final int appId = 1400563135;
        public static final String appKey = "affc01b5b3f939c5cdb0775dd3cc920f";
        // 模板内容: 您的注册验证码：{1}，{2}秒内有效，请勿泄露。
        public static final int templateIdRegisterCode = 1087068; // 玩家注册验证码短信模板
        public static final String smsSign = "修修录"; // 申请的签名
    }

    // PayJS支付平台配置
    public static class PayJS {
        public static final String mchid = "1613001888"; // 商户号
        public static final String key = "z62VEknlqCmBmQFC"; // 通信秘钥

        // 异步通知
        public final static String notifyUrl = "https://xxl.today/naicha-api/order/pay/notify";

        // API 地址
        public final static String nativeUrl = "https://payjs.cn/api/native";
        public final static String jsapiUrl = "https://payjs.cn/api/jsapi";
        public final static String micropayUrl = "https://payjs.cn/api/micropay";
        public final static String cashierUrl = "https://payjs.cn/api/cashier";
        public final static String checkUrl = "https://payjs.cn/api/check";
        public final static String closeUrl = "https://payjs.cn/api/close";
        public final static String reverseUrl = "https://payjs.cn/api/reverse";
        public final static String refundUrl = "https://payjs.cn/api/refund";
        public final static String infoUrl = "https://payjs.cn/api/info";
        public final static String complaintUrl = "https://payjs.cn/api/complaint";
        public final static String bankUrl = "https://payjs.cn/api/bank";
        public final static String mwebUrl = "https://payjs.cn/api/mweb";
    }

    // ******************** 缓存 ******************** //
    // key前缀
    public static class CacheKeyPrefix {
    }

    public static class CacheKey {
    }

    // ******************** 状态 ********************* //
    public final static byte player_status_normal = 1; // 账号正常
    public final static byte player_status_disabled = 2; // 账号已被冻结
    public final static byte player_status_forbidden = 3; // 账号已被封号

    // ****************** 时间/秒 ************************//
    public static final long COSNT_one_minute = 60;
    public static final long CONST_half_hour = COSNT_one_minute * 30;
    public static final long CONST_one_hour = CONST_half_hour * 2;
    public static final long CONST_one_day = CONST_one_hour * 24;
    public static final long CONST_one_week = CONST_one_day * 7;
    public static final long CONST_half_month = CONST_one_week * 2;
    public static final long CONST_one_month = CONST_half_month * 2;


    //********************** 普通常量 ***************************//
    // 令牌变量名
    public static final String CONST_token = "token";
    // 微信openid
    public static final String CONST_wx_openid = "wxOpenid";
    // sysUserId
    public static final String CONST_sys_user_id = "sysUserId";

    //********************* redis变量前缀 ***************************//
    // 验证码
    public static final String CONST_verify_code_redis_prefix = "verify_code:";
    // 锁
    public static final String CONST_lock_redis_prefix = "lock:";

    //************************ 缓存变量名称 ***************************//
    // 商品菜单列表缓存
    public static final String CONST_goods_menu_vo_cache = "goods_menu_vo_cache";
    // 小程序配置信息
    public static final String CONST_app_config = "app_config";
    // 用户会话缓存
    public static final String CONST_user_session_map = "user_session_map";
    // 管理员会话缓存
    public static final String CONST_sys_user_session_map = "sys_user_session_map";
    // 最新订单消息列表
    public static final String CONST_recent_order_message_map = "recent_order_message_map";

}
