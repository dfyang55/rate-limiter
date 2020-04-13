package com.dfy.ratelimiter.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: 段辉
 * @time: 2020/4/13 15:03
 */
public abstract class BucketLimit implements Limit {

    /** 桶最大容量 */
    protected int maxNumber;
    /** 时间单位 */
    protected TimeUnit timeUnit;
    /** 改变的数量 */
    protected int changeNumber;
    /** 改变的时间 */
    protected int changeTime;
    /** 桶中剩余数量 */
    protected AtomicInteger remainingNumber;

    public BucketLimit(int maxNumber, TimeUnit timeUnit, int changeNumber, int changeTime) {
        this.maxNumber = maxNumber;
        this.timeUnit = timeUnit;
        this.changeNumber = changeNumber;
        this.changeTime = changeTime;
        verifyParam();
    }

    protected void verifyParam() {
        if (maxNumber < 0 || changeNumber < 0 || changeTime < 0) {
            throw new IllegalArgumentException("参数必须大于0!");
        }
        if (maxNumber < changeNumber) {
            throw new IllegalArgumentException("maxNumber必须大于changeNumber!");
        }
    }
}
