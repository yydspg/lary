package cn.lary.core.ratelimiter.model.impl;

import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.Limit;

/**
 * @author paul 2024/4/15
 */

public class SlidingWindow extends Limit {
    public SlidingWindow(String K,Rate rate) {
        super(K,rate);
        this.size = rate.size();
    }


    private int  size;


    public int getSize() {
        return size;
    }
}
