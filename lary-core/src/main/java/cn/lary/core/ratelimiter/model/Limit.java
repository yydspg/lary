package cn.lary.core.ratelimiter.model;

import cn.lary.core.ratelimiter.anno.Rate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author paul 2024/4/15
 */
@Getter
@Setter
public class Limit {

    private String key;

    private TimeUnit unit;

    private String count;

    public Limit(String K,Rate rate) {
        this.key = K;
        this.unit = rate.unit();
        this.count = String.valueOf(rate.count());
    }
}
