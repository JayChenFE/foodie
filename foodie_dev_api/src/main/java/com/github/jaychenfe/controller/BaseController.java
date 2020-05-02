package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.vo.UserVO;
import com.github.jaychenfe.service.center.MyOrdersService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * @author jaychenfe
 */
@Controller
public class BaseController {

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
    private MyOrdersService myOrdersService;
    @Autowired
    private RedisOperator redisOperator;

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

    protected UserVO saveSessionAndConvertUserVO(Users user) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        String userTokenKey = "user_token:" + user.getId();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(userTokenKey, uniqueToken);
        return userVO;
    }

}
