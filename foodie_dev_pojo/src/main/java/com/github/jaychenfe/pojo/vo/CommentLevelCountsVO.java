package com.github.jaychenfe.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用于展示商品评价数量的vo
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class CommentLevelCountsVO {

    public Integer totalCounts;
    public Integer goodCounts;
    public Integer normalCounts;
    public Integer badCounts;
}
