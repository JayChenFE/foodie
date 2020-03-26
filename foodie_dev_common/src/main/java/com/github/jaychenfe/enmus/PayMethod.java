package com.github.jaychenfe.enmus;

/**
 * @author jaychenfe
 * @Description: 支付方式 枚举
 */
public enum PayMethod {

    /**
     * 微信
     */
    WEIXIN(1, "微信"),
    /**
     * 微信
     */
    ALIPAY(2, "支付宝");

    public final Integer type;
    public final String value;

    PayMethod(Integer type, String value){
        this.type = type;
        this.value = value;
    }

}
