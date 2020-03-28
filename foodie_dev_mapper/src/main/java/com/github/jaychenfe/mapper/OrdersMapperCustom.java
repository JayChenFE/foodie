package com.github.jaychenfe.mapper;


import com.github.jaychenfe.pojo.OrderStatus;
import com.github.jaychenfe.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
public interface OrdersMapperCustom {

    /**
     * 查询我的订单列表
     *
     * @param map 参数
     * @return 订单列表
     */
    List<MyOrdersVO> queryMyOrders(@Param("paramsMap") Map<String, Object> map);

    /**
     * 查询我的订单列表
     *
     * @param map 参数
     * @return 订单列表
     */
    int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);

    /**
     * 获取我的订单动向列表
     * @param map 参数
     * @return 订单动向列表
     */
    List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String, Object> map);

}
