package com.github.jaychenfe.service.center;

import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.vo.OrderStatusCountsVO;
import com.github.jaychenfe.utils.PagedGridResult;

/**
 * @author jaychenfe
 */
public interface MyOrdersService {

    /**
     * 查询我的订单列表
     *
     * @param userId      用户id
     * @param orderStatus 订单状态
     * @param page        当前页
     * @param pageSize    每页条数
     * @return 分页结果
     */
    PagedGridResult queryMyOrders(String userId,
                                  Integer orderStatus,
                                  Integer page,
                                  Integer pageSize);

    /**
     * 订单状态 --> 商家发货
     *
     * @param orderId 订单id
     */

    void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return 我的订单
     */
    Orders queryMyOrder(String userId, String orderId);

    /**
     * 更新订单状态 —> 确认收货
     *
     * @param orderId 订单id
     * @return 是否更新成功
     */
    boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单（逻辑删除）
     *
     * @param userId  用户id
     * @param orderId 更新id
     * @return 是否删除成功
     */
    boolean deleteOrder(String userId, String orderId);

    /**
     * 查询用户订单数
     *
     * @param userId 用户id
     * @return 用户订单数
     */
    OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 获得分页的订单动向
     *
     * @param userId   用户id
     * @param page     当前页数
     * @param pageSize 每页条数
     * @return 分页的订单动向
     */
    PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize);
}
