package cn.lary.core.constant;

/**
 * @author paul 2024/4/15
 */

public interface RateCS {
    interface Ploy {
        String SLIDING_WINDOW = "slidingWindow";
        String TOKEN_BUCKET = "tokenBucket";
        String FIXED_WINDOW = "fixedWindow";
    }

    interface Target{
        String UID = "uid";
        String IP = "ip";
        String El = "el";
    }

}
