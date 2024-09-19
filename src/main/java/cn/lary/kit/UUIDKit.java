package cn.lary.kit;

import java.math.BigInteger;
import java.util.UUID;

public class UUIDKit {
    private static final String CHAR_SET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int BASE = CHAR_SET.length();
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        // 将 UUID 转换为不带短横线的字符串
        return uuid.toString().replace("-", "");
    }


    // 将UUID转换为5个字符的字符串

    public static String uuidToShort(String uuid) {
        // 将UUID转换为BigInteger
        BigInteger least = new BigInteger(1, uuid.getBytes());
        StringBuilder shortUuid = new StringBuilder();
        // 使用基数转换算法
        for (int i = 0; i < 5; i++) {
            int remainder = least.mod(BigInteger.valueOf(BASE)).intValue();
            least = least.divide(BigInteger.valueOf(BASE));
            shortUuid.append(CHAR_SET.charAt(remainder));
        }
        return shortUuid.toString();
    }


}
