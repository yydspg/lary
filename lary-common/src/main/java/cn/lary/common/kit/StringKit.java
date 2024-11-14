package cn.lary.common.kit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class StringKit {

    private static final String AES_ALGORITHM = "AES";
    // AES密钥
    private static final String AES_SECRET_KEY = "4128D9CDAC7E2F82951CBAF7FDFE675B";
    // AES加密模式为GCM，填充方式为NoPadding
// AES-GCM 是流加密（Stream cipher）算法，所以对应的填充模式为 NoPadding，即无需填充。
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    // 加密器
    private static Cipher encryptionCipher;
    // 解密器
    private static Cipher decryptionCipher;

    static {
        // 将AES密钥转换为SecretKeySpec对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(AES_SECRET_KEY.getBytes(), AES_ALGORITHM);
        // 使用指定的AES加密模式和填充方式获取对应的加密器并初始化
        try {
            encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // 使用指定的AES加密模式和填充方式获取对应的解密器并初始化
            decryptionCipher = Cipher.getInstance(AES_TRANSFORMATION);
            decryptionCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(128, encryptionCipher.getIV()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean same(String str1, String str2) {return Objects.equals(str1, str2);}

    public static boolean diff(String str1, String str2) {return !Objects.equals(str1, str2);}

    public static String random(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }

    public static String MD5(String str) {
        MessageDigest md = null;
        if (str == null) {
            return null;
        }
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(str.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public static String[] split(String str, String regex) {
        if (isEmpty(str)) {
            return null;
        }
        return str.split(regex);
    }
    /**
     * 加密
     */
    public static String encrypt(String data)  {
        byte[] dataInBytes = data.getBytes();
        byte[] encryptedBytes = null;
        try {
            encryptedBytes = encryptionCipher.doFinal(dataInBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解密
     */
    public static String decrypt(String encryptedData) throws IllegalBlockSizeException, BadPaddingException {
        byte[] dataInBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = null;
        decryptedBytes = decryptionCipher.doFinal(dataInBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    /**
     * 如果给定字符串{@code str}中不包含{@code appendStr}<br>
     * 则在{@code str}后追加{@code appendStr}<br>
    * 如果已包含{@code appendStr}，则在{@code str}后追加{@code otherwise}
    */
    public static String appendIfNotContain(String str, String appendStr, String otherwise) {
        if (isEmpty(str) || isEmpty(appendStr)) {
            return str;
        }
        if (str.contains(appendStr)) {
            return str.concat(otherwise);
        }
        return str.concat(appendStr);
    }

    /**
     *  编码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     *解码
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }
}
