package cn.lary.core.util;

import cn.hutool.core.date.SystemClock;

/**
 * @author paul 2024/4/9
 */

public class DateKit {
    public static long currentTimeMillis() {
        return SystemClock.now();
    }
}
