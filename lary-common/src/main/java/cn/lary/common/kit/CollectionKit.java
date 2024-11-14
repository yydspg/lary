package cn.lary.common.kit;

import java.util.*;

public class CollectionKit {

    public static Set<Integer> generateUniqueRandomNumbers(int min, int max, int n) {
        Set<Integer> randomNumbers = new HashSet<>();
        if (n > (max - min + 1)) {
            for (int i = min; i <= max; i++) {
                randomNumbers.add(i);
            }
            return randomNumbers;
        }
        Random random = new Random();
        while (randomNumbers.size() < n) {
            int randomNumber = random.nextInt(max - min + 1) + min;
            randomNumbers.add(randomNumber);
        }
        return randomNumbers;
    }
    public static <T> List<T> removeRepeat(List<T> list ) {
        if (list == null) {
            return null;
        }
        HashSet<T> response = new HashSet<>(list);
        return new ArrayList<>(response);
    }
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
    public static boolean isNotEmpty(Collection collection) {
        return collection != null && collection.size() > 0;
    }
    public static boolean isEmpty(Map<?, ?> map) {
        return !isNotEmpty(map);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }
}
