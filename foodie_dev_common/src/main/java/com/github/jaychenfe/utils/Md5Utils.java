package com.github.jaychenfe.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author jaychenfe
 */
public class Md5Utils {
    /**
     * @param strValue strValue
     * @return String
     * @throws NoSuchAlgorithmException if an unKnow Algorithm value is given.
     * @Title: MD5Utils.java
     * @Package com.github.jaychenfe.utils
     * @Description: 对字符串进行md5加密
     */
    public static String getMd5Str(String strValue) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return Base64.encodeBase64String(md5.digest(strValue.getBytes()));
    }
}
