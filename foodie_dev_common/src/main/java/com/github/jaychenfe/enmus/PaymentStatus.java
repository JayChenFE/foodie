package com.github.jaychenfe.enmus;

/**
 * @author jaychenfe
 * @Description: 支付中心的支付状态 10：未支付 20：已支付 30：支付失败 40：已退款
 */
public enum PaymentStatus {

    /**
     * 未支付
     */

    WAIT_PAY(10, "未支付"),
    /**
     * 已支付
     */
    PAID(20, "已支付"),
    /**
     * 支付失败
     */
    PAY_FAILED(30, "支付失败"),
    /**
     * 已退款
     */
    SUCCESS(40, "已退款");

    public final Integer type;
    public final String value;

    PaymentStatus(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

}
