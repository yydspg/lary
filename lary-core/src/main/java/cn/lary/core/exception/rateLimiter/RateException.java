package cn.lary.core.exception.rateLimiter;

/**
 * @author paul 2024/4/14
 */

public class RateException extends RuntimeException {

    public RateException() {
        super();
    }

    public RateException(String message) {

        super(message);
    }
}
