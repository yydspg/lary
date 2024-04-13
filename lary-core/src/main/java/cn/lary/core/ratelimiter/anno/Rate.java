package cn.lary.core.ratelimiter.anno;


import cn.lary.core.constant.FreqTarget;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/14
 */
@Repeatable(Rate.List.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Rate {

    String ploy() default "";

    int windowSize() default 5;

    int period() default 1;

    String prefixKey() default "";

    FreqTarget target() default FreqTarget.EL;

    String spEl() default "";

    int time() default 10;
    
    TimeUnit unit() default TimeUnit.SECONDS;

    int count() default 1;

    long capacity() default 3;

    double refillRate() default 0.5;


    @Inherited
    @Target(value = {ElementType.METHOD})
    @Retention(value = RetentionPolicy.RUNTIME)
    @interface List {
        Rate[] value();
    }
}
