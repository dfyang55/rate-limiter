package com.dfy.ratelimiter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 滑动窗口计数器限流
 * @author: DFY
 * @time: 2020/4/8 17:01
 */
public class SlidingWindowCounterLimit extends CounterLimit {

    private static Logger logger = LoggerFactory.getLogger(SlidingWindowCounterLimit.class);

    /** 计数分布 */
    private AtomicInteger[] countDistribution;
    /** 当前时间在计数分布的索引 */
    private volatile int currentIndex;
    /** 当前时间之前的滑动窗口计数 */
    private int preTotalCount;

    public SlidingWindowCounterLimit(int limitCount, long limitTime) {
        this(limitCount, limitTime, TimeUnit.SECONDS);
    }

    public SlidingWindowCounterLimit(int limitCount, long limitTime, TimeUnit timeUnit) {
        this.limitCount = limitCount;
        this.limitTime = limitTime;
        this.timeUnit = timeUnit;
        countDistribution = new AtomicInteger[limitCount << 1];
    }

    @Override
    protected boolean tryCount() {
        if (limited) {
            return false;
        } else {
            if (preTotalCount + countDistribution[currentIndex].get() == 10) {
                logger.info("限流：{}", LocalDateTime.now().toString());
                limited = true;
                return false;
            } else {
                countDistribution[currentIndex].incrementAndGet();
                return true;
            }
        }
    }

    class CounterResetThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    timeUnit.sleep(1);
                    int indexToReset = currentIndex - limitCount - 1;
                    int tmp = countDistribution[indexToReset].get();
                    preTotalCount = preTotalCount - tmp
                            + countDistribution[currentIndex].get();
                    countDistribution[indexToReset].compareAndSet(tmp, 0);
                    currentIndex++;
                    limited = false; // 修改当前状态为不受限
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
