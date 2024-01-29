package com.lary.common.core.utils;

/**
 * @author paul 2023/12/30
 */

public class StringUtil {
    /**
     * @param str:
     * @param suffix: 后缀
     * @param ignoreCase: 是否忽略大小写
     * @param ignoreEquals: 是否忽略字符串相等
     * @return boolean
     * @author paul
     * @description TODO
     * @date 2023/12/30 1:56
     */
    public static boolean endWith(CharSequence str,CharSequence suffix,boolean ignoreCase,boolean ignoreEquals){
        if(str == null || suffix == null){
            if(ignoreCase){
                return false;
            }
            return null == str && null == suffix;
        }
        final int strOffset = str.length() - suffix.length();
        boolean isEndWith = str.toString()
                .regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());
        if (isEndWith) {
            return (!ignoreEquals) || (equals(str,suffix,ignoreCase));
        }
        return false;
    }
    public static boolean endWithIgnoreCase(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, true);
    }
    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase) {
        return endWith(str, suffix, ignoreCase, false);
    }
    public static boolean endWith(CharSequence str, CharSequence suffix) {
        return endWith(str, suffix, false);
    }
    public static boolean equals(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, false);
    }
    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return equals(str1, str2, true);
    }
    public static boolean equals(CharSequence str1,CharSequence str2,boolean ignoreCase){
        if (str1 == null) {
            //if str1 == null && str2 == null means true;
            return str2 == null;
        }
        if(str2 == null){
            return false;
        }
        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        }else {
            return str1.toString().contentEquals(str2.toString());
        }
    }

}
