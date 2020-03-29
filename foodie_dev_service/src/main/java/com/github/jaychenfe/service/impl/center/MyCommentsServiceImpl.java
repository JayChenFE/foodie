package com.github.jaychenfe.service.impl.center;

import com.github.jaychenfe.enmus.YesOrNo;
import com.github.jaychenfe.mapper.ItemsCommentsMapperCustom;
import com.github.jaychenfe.mapper.OrderItemsMapper;
import com.github.jaychenfe.mapper.OrderStatusMapper;
import com.github.jaychenfe.mapper.OrdersMapper;
import com.github.jaychenfe.pojo.OrderItems;
import com.github.jaychenfe.pojo.OrderStatus;
import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.bo.center.OrderItemsCommentBO;
import com.github.jaychenfe.pojo.vo.MyCommentVO;
import com.github.jaychenfe.service.MyCommentsService;
import com.github.jaychenfe.utils.PagedGridResult;
import com.github.jaychenfe.utils.PagedGridUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
@Service
public class MyCommentsServiceImpl implements MyCommentsService {

    private OrderItemsMapper orderItemsMapper;
    private OrdersMapper ordersMapper;
    private OrderStatusMapper orderStatusMapper;
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;
    private Sid sid;

    @Autowired
    public MyCommentsServiceImpl(OrderItemsMapper orderItemsMapper,
                                 OrdersMapper ordersMapper,
                                 OrderStatusMapper orderStatusMapper,
                                 ItemsCommentsMapperCustom itemsCommentsMapperCustom,
                                 Sid sid) {
        this.orderItemsMapper = orderItemsMapper;
        this.ordersMapper = ordersMapper;
        this.orderStatusMapper = orderStatusMapper;
        this.itemsCommentsMapperCustom = itemsCommentsMapperCustom;
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void saveComments(String orderId, String userId,
                             List<OrderItemsCommentBO> commentList) {

        // 1. 保存评价 items_comments
        for (OrderItemsCommentBO oic : commentList) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
        map.put("userId", userId);
        map.put("commentList", commentList);
        itemsCommentsMapperCustom.saveComments(map);

        // 2. 修改订单表改已评价 orders
        Orders order = new Orders();
        order.setId(orderId);
        order.setIsComment(YesOrNo.YES.type);
        ordersMapper.updateByPrimaryKeySelective(order);

        // 3. 修改订单状态表的留言时间 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult<MyCommentVO> queryMyComments(String userId,
                                                        Integer page,
                                                        Integer pageSize) {

        Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
        map.put("userId", userId);

        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        return PagedGridUtils.setterPagedGrid(list, page);
    }
}
