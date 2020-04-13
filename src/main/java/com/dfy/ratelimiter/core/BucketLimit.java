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

}
