package cn.lary.core.ratelimiter.ploy;

import cn.lary.core.exception.rateLimiter.RateException;
import cn.lary.core.ratelimiter.anno.Rate;
import cn.lary.core.ratelimiter.model.Limit;
import cn.lary.core.util.StringKit;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author paul 2024/4/15
 */

public abstract class RatePloy <K extends Limit> {
    @PostConstruct
    protected void register() {
            RatePloyFactory.register(getName(),this);
    }

    public abstract String getName();

    public abstract Boolean isLimit(K limit);


    @FunctionalInterface
    public interface Supplier<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface Executor {
        void execute() throws Throwable;
    }

    public <T> T execute(K limit, Supplier<T> supplier) throws Throwable {

        if (isLimit(limit)) {throw new RateException("reachLimit");}
        return supplier.get();
    }
}
