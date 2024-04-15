package cn.lary.core.ratelimiter.anno;


import cn.lary.core.constant.RateCS;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/14
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rate {

    String ploy() default RateCS.Ploy.SLIDING_WINDOW;

    int size() default 10;

    String spEl() default "";

    int time() default 10;
    
    TimeUnit unit() default TimeUnit.SECONDS;

    int count() default 1;

    long capacity() default 3;

    double refillRate() default 0.5;

}
