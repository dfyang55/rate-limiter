package com.dfy.ratelimiter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 漏桶限流
 * @author: DFY
 * @time: 2020/4/13 14:47
 */
public class LeakyBucketLimit extends BucketLimit {

    private static Logger logger = LoggerFactory.getLogger(LeakyBucketLimit.class);

    public LeakyBucketLimit(int leakNumber, int leakTime, int maxNumber) {
        this(leakNumber, leakTime, TimeUnit.SECONDS, maxNumber);
    }

    public LeakyBucketLimit(int leakNumber, int leakTime, TimeUnit timeUnit, int maxNumber) {
        super(maxNumber, timeUnit, leakNumber, leakTime);
        this.remainingNumber = new AtomicInteger(0);
        new Thread(new LeakThread()).start();
    }

    public boolean tryAcquire() {
        while (true) {
            int currentStoredNumber = remainingNumber.get();
            if (currentStoredNumber == maxNumber) {
                logger.info("限流：{}", LocalDateTime.now().toString());
                return false;
            }
            if (remainingNumber.compareAndSet(currentStoredNumber, currentStoredNumber + 1)) {
                return true;
            }
        }
    }

    class LeakThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (remainingNumber.get() == 0) {
                    logger.info("当前桶已空");
                    try { timeUnit.sleep(changeTime); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                } else {
                    int old =  remainingNumber.get();
                    int newValue = old - changeNumber;
                    if (newValue < 0)
                        newValue = 0;
                    remainingNumber.compareAndSet(old, newValue);
                    logger.info("泄露：{}，当前：{}", changeNumber, newValue);
                    try { timeUnit.sleep(changeTime); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }
    }
}
