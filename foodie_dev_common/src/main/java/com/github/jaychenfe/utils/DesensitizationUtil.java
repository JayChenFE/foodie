package com.github.jaychenfe.utils;

/**
 * 通用脱敏工具类
 * 可用于：
 * 用户名
 * 手机号
 * 邮箱
 * 地址等
 *
 * @author jaychenfe
 */
public class DesensitizationUtil {

    private static final int SIZE = 6;
    private static final String SYMBOL = "*";

    /**
     * 通用脱敏方法
     *
     * @param value 原值
     * @return 脱敏值
     */
    public static String commonDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
            return stringBuilder.toString();
        }

        if (pamatwo <= 0) {
            stringBuilder.append(value, 0, 1);
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value, len - 1, len);

        } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
            int pamafive = (len - SIZE) / 2;
            stringBuilder.append(value, 0, pamafive);
            for (int i = 0; i < SIZE; i++) {
                stringBuilder.append(SYMBOL);
            }
            stringBuilder.append(value, len - (pamafive + 1), len);
        } else {
            int pamafour = len - 2;
            stringBuilder.append(value, 0, 1);
            for (int i = 0; i < pamafour; i++) {
                stringBuilder.append(SYMBOL);
            }
            stringBuilder.append(value, len - 1, len);
        }
        return stringBuilder.toString();
    }

}
