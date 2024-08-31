package cn.lary.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class CollectionKit {
    public static <T> List<T> removeRepeat(List<T> list ) {
        if (list == null) {
            return null;
        }
        HashSet<T> res = new HashSet<>(list);
        return new ArrayList<>(res);
    }
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }
}
