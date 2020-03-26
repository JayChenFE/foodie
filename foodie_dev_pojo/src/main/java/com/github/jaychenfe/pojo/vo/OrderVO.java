package com.github.jaychenfe.pojo.vo;

/**
 * @author jaychenfe
 */
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }

    @Override
    public String toString() {
        return "OrderVO{" +
                "orderId='" + orderId + '\'' +
                ", merchantOrdersVO=" + merchantOrdersVO +
                '}';
    }
}
