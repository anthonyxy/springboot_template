package com.xyz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义用户登录注解，用于对需要登录才能访问的接口进行拦截，并解析userId作为参数传入
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {

    // 是否必须登录（必须登陆为true，可能登录为false，无须登录即不使用该注解）
    boolean isRequisite() default true;

    // 允许角色访问的标志（多个以,分隔）
    String role() default "";

    // 是否自动注入
    boolean isUse() default true;

    // 如果使用自动注入，接收id的参数的名
    String value() default "userId";

    // 使用参数接收的位数
    int paramIndex() default 0;

    // 指明获取参数类型的方式
    Type getType() default Type.HEADTOKEN;

    enum Type {
        HEADTOKEN, // 生成token放在请求头
        PARAMID, // 通过参数直传userId
        COOKIE // 生成token放到cookie中（暂无）
    }

}
