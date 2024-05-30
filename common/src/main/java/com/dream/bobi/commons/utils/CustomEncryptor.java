package com.dream.bobi.commons.utils;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base32;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class CustomEncryptor {
    private static final int KEY_SIZE = 128; // AES密钥大小
    private static final int OUTPUT_LENGTH = 8; // 8个字符

    // 生成AES密钥
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }

    // 对字符串进行AES加密并使用Base32编码
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        Base32 base32 = new Base32();
        String base32Encoded = base32.encodeToString(encryptedBytes);
        return base32Encoded.substring(0, OUTPUT_LENGTH); // 截取前8位字符
    }

    public static String encryptInt(int src){
        SecretKey secretKey = null;
        try {
            secretKey = generateKey();
            return encrypt(String.valueOf(src),secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}