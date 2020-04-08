package com.dfy.ratelimiter.annotation;

import com.dfy.ratelimiter.enums.LimitModeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 对接口进行限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    /** 限流模式 */
    LimitModeEnum mode();

    /** 单位时间生成多少个令牌 */
    double rate();

    /** 超时时间 */
    long timeout();

    /** 时间单位 */
    TimeUnit timeunit() default TimeUnit.SECONDS;
}
