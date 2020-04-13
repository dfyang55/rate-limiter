package com.dfy.ratelimiter.core;

import java.util.concurrent.TimeUnit;

/**
 * @description: 计数器限流
 * @author: DFY
 * @time: 2020/4/8 17:02
 */
public abstract class CounterLimit implements Limit {

    /** 单位时间限制数 */
    protected int limitCount;
    /** 限制时间 */
    protected long limitTime;
    /** 时间单位，默认为秒 */
    protected TimeUnit timeUnit;

    /** 当前是否为受限状态 */
    protected volatile boolean limited;
}
