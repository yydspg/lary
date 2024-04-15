package cn.lary.core.util;

import cn.hutool.core.util.StrUtil;

/**
 * @author paul 2024/4/9
 */

public class StringKit extends StrUtil {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }
}
