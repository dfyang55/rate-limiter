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

    /** 格子分布 */
    private AtomicInteger[] gridDistribution;
    /** 当前时间在计数分布的索引 */
    private volatile int currentIndex;
    /** 当前时间之前的滑动窗口计数 */
    private int preTotalCount;
    /** 格子数 */
    private final int gridNumber;
    /** 是否正在执行状态重置 */
    private volatile boolean resetting;

    public SlidingWindowCounterLimit(int gridNumber, int limitCount, long limitTime) {
        this(gridNumber, limitCount, limitTime, TimeUnit.SECONDS);
    }

    public SlidingWindowCounterLimit(int gridNumber, int limitCount, long limitTime, TimeUnit timeUnit) {
        super(limitCount, limitTime, timeUnit);
        if (gridNumber <= limitTime)
            throw new RuntimeException("无法完成限流，gridNumber必须大于limitTime，gridNumber = " + gridNumber + ",limitTime = " + limitTime);
        this.gridNumber = gridNumber;
        gridDistribution = new AtomicInteger[gridNumber];
        for (int i = 0; i < gridNumber; i++) {
            gridDistribution[i] = new AtomicInteger(0);
        }
        new Thread(new CounterResetThread()).start();
    }

    public boolean tryAcquire() {
        while (true) {
            if (limited) {
                return false;
            } else {
                int currentGridCount = gridDistribution[currentIndex].get();
                if (preTotalCount + currentGridCount == changeNumber) {
                    logger.info("限流：{}", LocalDateTime.now().toString());
                    limited = true;
                    return false;
                }
                if (!resetting && gridDistribution[currentIndex].compareAndSet(currentGridCount, currentGridCount + 1))
                    return true;
            }
        }
    }

    class CounterResetThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    timeUnit.sleep(1); // 停止1个时间单位
                    int indexToReset = currentIndex - changeNumber - 1; // 要重置计数的格子索引
                    if (indexToReset < 0) indexToReset += gridNumber;
                    resetting = true; // 防止在更新状态时，用户访问接口将当前格子的访问量 + 1
                    preTotalCount = preTotalCount - gridDistribution[indexToReset].get()
                            + gridDistribution[currentIndex++].get(); // 重置当前时间之前的滑动窗口计数
                    if (currentIndex == gridNumber) currentIndex = 0;
                    if (preTotalCount + gridDistribution[currentIndex].get() < changeNumber)
                        limited = false; // 修改当前状态为不受限
                    resetting = false;
                    logger.info("当前格子：{}，重置格子：{}，重置格子访问量：{}，前窗口格子总数：{}",
                            currentIndex, indexToReset, gridDistribution[indexToReset].get(), preTotalCount);
                    gridDistribution[indexToReset].set(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
