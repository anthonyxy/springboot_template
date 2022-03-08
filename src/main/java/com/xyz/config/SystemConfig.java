package com.xyz.config;

// 系统配置
public class SystemConfig {

    // 用户登录过期时间（天）
    public final static long LOGIN_OUT_TIME = 9999;

    // 登录后用户身份请求头
    public final static String HEAD_TOKEN = "User-Token";

    // 后台登录后用户身份请求头
    public final static String ACCOUNT_TOKEN = "Admin-Token";

    // 后台登录过期时间（秒）
    // public final static long ADMIN_LOGIN_OUT_TIME = 3 * 24 * 60 * 60;

    // 后台操作过期时间（秒）
    // public final static long ADMIN_OPERATION_OUT_TIME = 6 * 60 * 60;


}
