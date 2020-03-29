package com.github.jaychenfe.mapper;

import com.github.jaychenfe.my.mapper.MyMapper;
import com.github.jaychenfe.pojo.ItemsComments;
import com.github.jaychenfe.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    /**
     * 保存我的评论
     *
     * @param map 参数
     */
    void saveComments(Map<String, Object> map);

    /**
     * 查询我的评论列表
     *
     * @param map 参数
     * @return 我的评论列表
     */
    List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);
}
