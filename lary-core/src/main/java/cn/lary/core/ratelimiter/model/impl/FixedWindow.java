package cn.lary.core.ratelimiter.model.impl;

import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.Limit;

/**
 * @author paul 2024/4/15
 */

public class FixedWindow extends Limit  {

    public FixedWindow(String K, Rate rate) {
        super(K,rate);
        this.time = rate.time();
    }

    private Integer time;

}
