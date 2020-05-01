package com.github.jaychenfe.pojo.vo;

import com.github.jaychenfe.pojo.bo.ShopCartBO;

import java.util.List;

/**
 * @author jaychenfe
 */
public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopCartBO> toRemoveShopCartBOList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public List<ShopCartBO> getToRemoveShopCartBOList() {
        return toRemoveShopCartBOList;
    }

    public void setToRemoveShopCartBOList(List<ShopCartBO> toRemoveShopCartBOList) {
        this.toRemoveShopCartBOList = toRemoveShopCartBOList;
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
