package com.xyz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口参数校验<br>
 * 只支持用String类型接收的参数
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    // 是否必须
    boolean isRequired() default true;

    // 参数类型
    ParamType type() default ParamType.PLAIN;

    // 自定义二级规则（ParamType为PLAIN时为正则，ParamType为QUALIFIER时为限定值（以,分隔），ParamType为DATE时为时间格式yMd）
    String rule() default "";

    // 发生错误后传给前端的提示语
    String warn() default "";

    // 参数类型
    enum ParamType {

        // 默认普通类型，支持rule自定义正则
        PLAIN(""),

        // 特定的取值如("-1,2,3")，必须使用rule自定义
        QUALIFIER(""),

        // 手机号
        MOBILE(""),

        // 正整数
        POSITIVE_INTEGER(""),

        // 时间，默认yyyy-MM-dd， 支持rule自定义
        DATE("yyyy-MM-dd"),

        // 邮箱
        EMAIL("");

        private final String value;

        public String value() {
            return value;
        }

        ParamType(String value) {
            this.value = value;
        }

    }

}
