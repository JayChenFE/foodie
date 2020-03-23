package com.github.jaychenfe.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 二级分类VO
 *
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class CategoryVO {

    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;

    /**
     * 三级分类vo list
     */
    private List<SubCategoryVO> subCatList;

}
