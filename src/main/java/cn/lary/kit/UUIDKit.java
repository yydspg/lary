package cn.lary.kit;

import java.util.UUID;

public class UUIDKit {
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        // 将 UUID 转换为不带短横线的字符串
        return uuid.toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
