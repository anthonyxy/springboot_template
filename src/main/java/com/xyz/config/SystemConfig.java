package com.xyz.config;

// 系统配置
public class SystemConfig {

    // 用户登录过期时间（天）
    public final static long LOGIN_OUT_TIME = 90;

    // 登录后用户身份请求头
    public final static String HEAD_TOKEN = "User-Token";

    // 后台登录过期时间（秒）
    // public final static long ADMIN_LOGIN_OUT_TIME = 3 * 24 * 60 * 60;

    // 后台操作过期时间（秒）
    // public final static long ADMIN_OPERATION_OUT_TIME = 6 * 60 * 60;

    // 后台登录后用户身份请求头
    // public final static String ACCOUNT_TOKEN = "Admin-Token";

    // 是否启用签名（上线or测试）
    public final static boolean IS_SIGN = false;

    // 是否启用签名过期
    public final static boolean IS_SIGN_PAST = true;

    // 签名过期时间（秒）
    public final static int SIGN_PAST_TIME = 10;

    // 全局签名
    public final static String SIGN = "arga298gu89wh6h843gv4";

    // 签名验证时加密签名的key
    public final static String SIGN_KEY = "sign";

    // 签名验证时时间戳的key
    public final static String TIMESTAMP_KEY = "timestamp";

    // 签名验证时随机数的key
    public final static String NONCE_KEY = "nonce";

}
