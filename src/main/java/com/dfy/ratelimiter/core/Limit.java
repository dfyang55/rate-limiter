package com.dfy.ratelimiter.core;

/**
 * @description: 限流接口
 * @author: DFY
 * @time: 2020/4/13 15:08
 */
public interface Limit {

    boolean tryAcquire();

}
