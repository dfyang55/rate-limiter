package com.dfy.ratelimiter.core;

import java.util.concurrent.TimeUnit;

/**
 * @description: 计数器限流
 * @author: DFY
 * @time: 2020/4/8 17:02
 */
public abstract class CounterLimit {

    /** 单位时间限制数 */
    protected int limitCount;
    /** 限制时间 */
    protected long limitTime;
    /** 时间单位，默认为秒 */
    protected TimeUnit timeUnit;

    /** 当前是否为受限状态 */
    protected volatile boolean limited;

    /**
     * 尝试将计数器加1，返回为true表示能够正常访问接口，false表示访问受限
     * @return
     */
    protected abstract boolean tryCount();
}
