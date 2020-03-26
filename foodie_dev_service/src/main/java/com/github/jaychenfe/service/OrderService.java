package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.OrderStatus;
import com.github.jaychenfe.pojo.bo.SubmitOrderBO;
import com.github.jaychenfe.pojo.vo.OrderVO;

public interface OrderService {

    /**
     * 用于创建订单相关信息
     *
     * @param submitOrderBO submitOrderBO
     * @return OrderVO
     */
    OrderVO createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId     订单id
     * @param orderStatus 订单状态
     */
    void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     *
     * @param orderId 订单id
     * @return 订单状态
     */
    OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    void closeOrder();
}
