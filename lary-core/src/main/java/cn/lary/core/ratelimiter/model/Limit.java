package cn.lary.core.ratelimiter.model;

import cn.lary.core.ratelimiter.anno.Rate;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/15
 */
@Data
public class Limit {

    private String key;

    private TimeUnit unit;

    private Integer count;

    public Limit(String K,Rate rate) {
        this.key = K;
        this.unit = rate.unit();
        this.count = rate.count();
    }
}
