package com.github.jaychenfe.service.impl;

import com.github.jaychenfe.enmus.CommentLevel;
import com.github.jaychenfe.mapper.ItemsCommentsMapper;
import com.github.jaychenfe.mapper.ItemsImgMapper;
import com.github.jaychenfe.mapper.ItemsMapper;
import com.github.jaychenfe.mapper.ItemsMapperCustom;
import com.github.jaychenfe.mapper.ItemsParamMapper;
import com.github.jaychenfe.mapper.ItemsSpecMapper;
import com.github.jaychenfe.pojo.Items;
import com.github.jaychenfe.pojo.ItemsComments;
import com.github.jaychenfe.pojo.ItemsImg;
import com.github.jaychenfe.pojo.ItemsParam;
import com.github.jaychenfe.pojo.ItemsSpec;
import com.github.jaychenfe.pojo.vo.CommentLevelCountsVO;
import com.github.jaychenfe.pojo.vo.ItemCommentVO;
import com.github.jaychenfe.pojo.vo.SearchItemsVO;
import com.github.jaychenfe.pojo.vo.ShopCartVO;
import com.github.jaychenfe.service.ItemService;
import com.github.jaychenfe.utils.DesensitizationUtil;
import com.github.jaychenfe.utils.PagedGridResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jaychenfe
 */
@Service
public class ItemServiceImpl implements ItemService {

    private ItemsMapper itemsMapper;
    private ItemsImgMapper itemsImgMapper;
    private ItemsSpecMapper itemsSpecMapper;
    private ItemsParamMapper itemsParamMapper;
    private ItemsCommentsMapper itemsCommentsMapper;
    private ItemsMapperCustom itemsMapperCustom;


    @Autowired
    public ItemServiceImpl(ItemsMapper itemsMapper,
                           ItemsImgMapper itemsImgMapper,
                           ItemsSpecMapper itemsSpecMapper,
                           ItemsParamMapper itemsParamMapper,
                           ItemsCommentsMapper itemsCommentsMapper,
                           ItemsMapperCustom itemsMapperCustom) {
        this.itemsMapper = itemsMapper;
        this.itemsImgMapper = itemsImgMapper;
        this.itemsSpecMapper = itemsSpecMapper;
        this.itemsParamMapper = itemsParamMapper;
        this.itemsCommentsMapper = itemsCommentsMapper;
        this.itemsMapperCustom = itemsMapperCustom;
    }


    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemsImgExp = new Example(ItemsImg.class);
        Example.Criteria criteria = itemsImgExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsImgMapper.selectByExample(itemsImgExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemsSpecExp = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemsSpecExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsSpecMapper.selectByExample(itemsSpecExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example itemsParamExp = new Example(ItemsParam.class);
        Example.Criteria criteria = itemsParamExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);

        return itemsParamMapper.selectOneByExample(itemsParamExp);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
        countsVO.setTotalCounts(totalCounts);
        countsVO.setGoodCounts(goodCounts);
        countsVO.setNormalCounts(normalCounts);
        countsVO.setBadCounts(badCounts);

        return countsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }

        return setterPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);

        return setterPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);

        return setterPagedGrid(list, page);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {

        List<String> specIdsList = Stream.of(specIds.split(","))
                .collect(Collectors.toList());

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }


    private PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (level != null) {
            condition.setCommentLevel(level);
        }

        return itemsCommentsMapper.selectCount(condition);
    }


}
