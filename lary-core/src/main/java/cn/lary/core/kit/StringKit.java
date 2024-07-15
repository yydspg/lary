package cn.lary.core.kit;

import java.util.Collection;

public class StringKit {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }
    public static boolean same(String str1, String str2) {return str1 == null ? str2 == null : str1.equals(str2);}
    public static boolean diff(String str1, String str2) {return str1 == null ? str2 != null : !str1.equals(str2);}
}
