package com.github.jaychenfe.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class SubCategoryVO {

    private Integer subId;
    private String subName;
    private String subType;
    private Integer subFatherId;
}
