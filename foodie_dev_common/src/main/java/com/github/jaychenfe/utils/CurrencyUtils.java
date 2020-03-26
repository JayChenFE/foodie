package com.github.jaychenfe.utils;

import java.math.BigDecimal;

/**
 * @author jaychenfe
 * @version V1.0
 * @Title: CurrencyUtils.java
 * @Description: 货币utils
 */
public class CurrencyUtils {

    public static String getFen2YuanWithPoint(Integer amount) {
        return BigDecimal.valueOf(amount).divide(new BigDecimal(100)).toString();
    }


    public static Integer getFen2Yuan(Integer amount) {
        return BigDecimal.valueOf(amount).divide(new BigDecimal(100)).intValue();
    }

    public static Integer getYuan2Fen(Integer amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).intValue();
    }

    public static Integer getYuan2Fen(String amount) {
        return BigDecimal.valueOf(Double.parseDouble(amount)).multiply(new BigDecimal(100)).intValue();
    }


}
