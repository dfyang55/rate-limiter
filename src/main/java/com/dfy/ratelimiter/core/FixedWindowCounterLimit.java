package com.dfy.ratelimiter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 固定窗口计数器限流
 * @author: DFY
 * @time: 2020/4/8 15:50
 */
public class FixedWindowCounterLimit extends CounterLimit {

    private static Logger logger = LoggerFactory.getLogger(FixedWindowCounterLimit.class);

    /** 计数器 */
    private AtomicInteger counter = new AtomicInteger(0);

    public FixedWindowCounterLimit(int limitCount, long limitTime) {
        this(limitCount, limitTime, TimeUnit.SECONDS);
    }

    public FixedWindowCounterLimit(int limitCount, long limitTime, TimeUnit timeUnit) {
        super(limitCount, limitTime, timeUnit);
        new Thread(new CounterResetThread()).start(); // 开启计数器清零线程
    }

    public boolean tryAcquire() {
        while (true) {
            if (limited) {
                return false;
            } else {
                int currentCount = counter.get();
                if (currentCount == changeNumber) {
                    logger.info("限流：{}", LocalDateTime.now().toString());
                    limited = true;
                    return false;
                } else {
                    if (counter.compareAndSet(currentCount, currentCount + 1))
                        return true;
                }
            }
        }
    }

    class CounterResetThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    timeUnit.sleep(changeTime);
                    counter.compareAndSet(changeNumber, 0); // 计数器清零
                    limited = false; // 修改当前状态为不受限
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
