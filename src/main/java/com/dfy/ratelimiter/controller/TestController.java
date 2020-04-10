package com.dfy.ratelimiter.controller;

import com.dfy.ratelimiter.core.FixedWindowCounterLimit;
import com.dfy.ratelimiter.core.SlidingWindowCounterLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 限流测试接口
 * @author: 段辉
 * @time: 2020/4/8 15:29
 */
@RequestMapping("/test")
@RestController
public class TestController {

    private FixedWindowCounterLimit fixedWindowCounterLimit = new FixedWindowCounterLimit(10, 10);

    private SlidingWindowCounterLimit slidingWindowCounterLimit = new SlidingWindowCounterLimit(20, 10, 10);

    @GetMapping("/hello")
    public String hello() {
        if (!slidingWindowCounterLimit.tryCount()) {
            return "限流!";
        }
        return "hello world!";
    }

}
