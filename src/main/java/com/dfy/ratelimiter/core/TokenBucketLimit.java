package com.dfy.ratelimiter.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 令牌桶限流
 * @author: DFY
 * @time: 2020/4/10 15:35
 */
public class TokenBucketLimit {

    private static Logger logger = LoggerFactory.getLogger(TokenBucketLimit.class);

    /** 给定时间生成令牌数 */
    private int genNumber;
    /** 生成令牌所花费的时间 */
    private int genTime;
    /** 时间单位，默认为秒 */
    private TimeUnit timeUnit;
    /** 最大令牌数 */
    private int maxNumber;
    /** 已存储的令牌数 */
    private AtomicInteger storedNumber;

    public TokenBucketLimit(int genNumber, int genTime, int maxNumber) {
        this(genNumber, genTime, TimeUnit.SECONDS, maxNumber);
    }

    public TokenBucketLimit(int genNumber, int genTime, TimeUnit timeUnit, int maxNumber) {
        this.genNumber = genNumber;
        this.genTime = genTime;
        this.timeUnit = timeUnit;
        this.maxNumber = maxNumber;
        this.storedNumber = new AtomicInteger(0);
        new Thread(new TokenGenerateThread()).start();
    }

    public boolean tryAcquire() {
        while (true) {
            int currentStoredNumber = storedNumber.get();
            if (currentStoredNumber == 0) {
                logger.info("限流：{}", LocalDateTime.now().toString());
                return false;
            }
            if (storedNumber.compareAndSet(currentStoredNumber, currentStoredNumber - 1)) {
                return true;
            }
        }
    }

    class TokenGenerateThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (storedNumber.get() == maxNumber) {
                    logger.info("当前令牌数已满");
                    try { timeUnit.sleep(genTime); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                } else {
                    int old =  storedNumber.get();
                    int newValue = old + genNumber;
                    if (newValue > maxNumber)
                        newValue = maxNumber;
                    storedNumber.compareAndSet(old, newValue);
                    logger.info("生成令牌数：{}，当前令牌数：{}", genNumber, newValue);
                    try { timeUnit.sleep(genTime); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        }
    }
}
