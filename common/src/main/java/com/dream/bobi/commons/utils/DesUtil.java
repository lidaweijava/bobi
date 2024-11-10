package com.dream.bobi.commons.utils;


import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

public class DesUtil {
    private final static Logger log = LoggerFactory.getLogger(DesUtil.class);
    private static final String DEFAULT_ENCODE = "utf-8";
    private static final String DES = "DES";
    private static final String PADDING = "DES/ECB/PKCS5Padding";
    private static final String key = "xhlb@qwofan197f#";

    public static String encrypt(String data){
        try {
            byte[] code = data.getBytes(DEFAULT_ENCODE);
            SecureRandom sr = new SecureRandom();
            //生成密钥
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            //加密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
            return Base64.encodeBase64String(cipher.doFinal(code));
        }catch (Exception e) {
            log.error("DesUtil encrypt error, the data:{}", data, e);
            return null;
        }
    }

    public static String decrypt(String data){
        try {
            byte[] value = Base64.decodeBase64(data);
            SecureRandom sr = new SecureRandom();
            //生成密钥
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            //解密
            Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
            return new String(cipher.doFinal(value), DEFAULT_ENCODE);
        }catch (Exception e) {
            log.error("DesUtil decrypt error, the data:{}", data, e);
            return null;
        }
    }

    public static void main(String[] args) {
        String a = encrypt("libo");
        String b = decrypt(a);
        System.out.println();
    }
}
