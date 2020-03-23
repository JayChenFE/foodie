package com.github.jaychenfe.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 最新商品VO
 *
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class NewItemsVO {

    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;

    private List<SimpleItemVO> simpleItemList;
}
