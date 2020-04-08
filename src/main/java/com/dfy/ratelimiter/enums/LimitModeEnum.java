package com.dfy.ratelimiter.enums;

/**
 * 限流模式枚举
 */
public enum LimitModeEnum {
    COUNTER, // 计数器
    LEAKY_BUCKET, // 漏桶
    TOKEN_BUCKET, // 令牌桶
}
