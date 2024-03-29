package com.lary.payment.kit.core.enums;

/**
 * @author paul 2024/1/29
 */

public enum SignType {
    /**
     * HMAC-SHA256 加密
     */
    HMACSHA256("HMAC-SHA256"),
    /**
     * MD5 加密
     */
    MD5("MD5"),
    /**
     * RSA
     */
    RSA("RSA");

    SignType(String type) {
        this.type = type;
    }

    private final String type;

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
