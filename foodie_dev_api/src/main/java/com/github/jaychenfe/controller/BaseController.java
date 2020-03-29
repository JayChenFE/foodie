package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.service.center.MyOrdersService;
import com.github.jaychenfe.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author jaychenfe
 */
@Controller
public class BaseController {

    public static final String FOODIE_SHOP_CART = "shopcart";

    /**
     * 支付中心的调用地址
     */
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";
    /**
     * 微信支付成功 -> 支付中心 -> 天天吃货平台
     * |-> 回调通知的url
     */
    String payReturnUrl = "http://api.z.mukewang.com/foodie-dev-api/orders/notifyMerchantOrderPaid";

    @Autowired
    protected MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return ApiResponse
     */
    protected ApiResponse checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return ApiResponse.errorMsg("订单不存在！");
        }
        return ApiResponse.ok(order);
    }

}
