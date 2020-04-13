package com.dfy.ratelimiter.core;

import java.util.concurrent.TimeUnit;

/**
 * @description: 计数器限流
 * @author: DFY
 * @time: 2020/4/8 17:02
 */
public abstract class CounterLimit implements Limit {

    /** 单位时间限制数 */
    protected int changeNumber;
    /** 限制时间 */
    protected long changeTime;
    /** 时间单位，默认为秒 */
    protected TimeUnit timeUnit;

    /** 当前是否为受限状态 */
    protected volatile boolean limited;

    public CounterLimit(int changeNumber, long changeTime, TimeUnit timeUnit) {
        this.changeNumber = changeNumber;
        this.changeTime = changeTime;
        this.timeUnit = timeUnit;
        verifyParam();
    }

    private void verifyParam() {
        if (changeNumber < 0 || changeTime < 0) {
            throw new IllegalArgumentException("参数必须大于0!");
        }
    }
}
