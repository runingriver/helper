package org.helper.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zongzhehu on 17-1-20.
 */
public class decryptAndencrypt {

    private static final Logger logger = LoggerFactory.getLogger(decryptAndencrypt.class);

    /**
     * md5的使用方法,md5只能加密,不能将md5还原
     */
    private static String md5(final String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("{}", e);
        }
        final byte[] messageDigest = md.digest(input.getBytes());
        final BigInteger number = new BigInteger(1, messageDigest);
        return String.format("%032x", number);
    }

    // 下面实现加解密方法
    private static final String SALT = "hld/.(&%uq3298@%^+347e";
    private static byte[] sharedvector = { 0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11 };

    public static String EncryptText(String RawText) {
        String EncText = "";
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;
        byte[] toEncryptArray;

        try {
            toEncryptArray = RawText.getBytes("UTF-8");
            MessageDigest m = MessageDigest.getInstance("MD5");
            temporaryKey = m.digest(SALT.getBytes("UTF-8"));

            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; i++) {
                    keyArray[i] = temporaryKey[index];
                }
            }

            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedvector));
            byte[] encrypted = c.doFinal(toEncryptArray);
            EncText = Base64.encodeBase64String(encrypted);

        } catch (Exception e) {
            logger.error("加密失败", e);
        }
        return EncText;
    }

    public static String DecryptText(String EncText) {
        String RawText = "";
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            temporaryKey = m.digest(SALT.getBytes("UTF-8"));

            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; i++) {
                    keyArray[i] = temporaryKey[index];
                }
            }

            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(sharedvector));
            byte[] decrypted = c.doFinal(Base64.decodeBase64(EncText));

            RawText = new String(decrypted, "UTF-8");
        } catch (Exception e) {
            logger.error("解密失败", e);
        }
        return RawText;
    }

}
