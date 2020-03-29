package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.OrderItems;
import com.github.jaychenfe.pojo.bo.center.OrderItemsCommentBO;
import com.github.jaychenfe.pojo.vo.MyCommentVO;
import com.github.jaychenfe.utils.PagedGridResult;

import java.util.List;

/**
 * @author jaychenfe
 */
public interface MyCommentsService {

    /**
     * 根据订单id查询关联的商品
     *
     * @param orderId 订单id
     * @return 关联的商品
     */
    List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户的评论
     *
     * @param orderId     订单id
     * @param userId      用户id
     * @param commentList 评价列表
     */
    void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList);


    /**
     * 我的评价查询 分页
     *
     * @param userId   用户id
     * @param page     当前页
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PagedGridResult<MyCommentVO> queryMyComments(String userId, Integer page, Integer pageSize);
}
