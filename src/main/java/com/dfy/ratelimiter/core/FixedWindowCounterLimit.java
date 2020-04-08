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

    /** 计数器清零线程 */
    private Thread counterResetThread;

    public FixedWindowCounterLimit(int limitCount, long limitTime) {
        this(limitCount, limitTime, TimeUnit.SECONDS);
    }

    public FixedWindowCounterLimit(int limitCount, long limitTime, TimeUnit timeUnit) {
        this.limitCount = limitCount;
        this.limitTime = limitTime;
        this.timeUnit = timeUnit;
        counterResetThread = new Thread(new CounterResetThread());
        counterResetThread.start();
    }

    public boolean tryCount() {
        if (limited) {
            return false;
        } else {
            if (counter.get() == 10) {
                logger.info("限流：{}", LocalDateTime.now().toString());
                limited = true;
                return false;
            } else {
                counter.incrementAndGet();
                return true;
            }
        }
    }

    class CounterResetThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    timeUnit.sleep(limitTime);
                    counter.compareAndSet(limitCount, 0); // 计数器清零
                    limited = false; // 修改当前状态为不受限
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
