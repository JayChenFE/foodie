package com.github.jaychenfe.pojo.vo;

import com.github.jaychenfe.pojo.Items;
import com.github.jaychenfe.pojo.ItemsImg;
import com.github.jaychenfe.pojo.ItemsParam;
import com.github.jaychenfe.pojo.ItemsSpec;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 商品详情VO
 *
 * @author jaychenfe
 */
@Getter
@Setter
@ToString
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}