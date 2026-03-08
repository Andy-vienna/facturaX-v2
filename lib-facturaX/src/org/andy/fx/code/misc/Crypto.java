package org.andy.fx.code.misc;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Crypto {
    private static final String KEY = "facturaXv2crypto"; // 16 Zeichen für AES

    public static String encrypt(String value) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        return Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes()));
    }

    public static String decrypt(String encrypted) throws Exception {
        SecretKeySpec sks = new SecretKeySpec(KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));
    }
}
