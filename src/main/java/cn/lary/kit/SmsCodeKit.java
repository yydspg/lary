package cn.lary.kit;

import java.security.SecureRandom;

public class SmsCodeKit {
    private final static SecureRandom secureRandom = new SecureRandom();

    public static String getToken() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }
}
