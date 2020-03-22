package com.github.jaychenfe.service.impl;

import com.github.jaychenfe.mapper.CategoryMapper;
import com.github.jaychenfe.mapper.CategoryMapperCustom;
import com.github.jaychenfe.pojo.Category;
import com.github.jaychenfe.pojo.vo.CategoryVO;
import com.github.jaychenfe.pojo.vo.NewItemsVO;
import com.github.jaychenfe.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jaychenfe
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryMapper categoryMapper;
    private CategoryMapperCustom categoryMapperCustom;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper, CategoryMapperCustom categoryMapperCustom) {
        this.categoryMapper = categoryMapper;
        this.categoryMapperCustom = categoryMapperCustom;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<Category> queryAllRootLevelCat() {

        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", 1);

        List<Category> result = categoryMapper.selectByExample(example);

        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {
        return categoryMapperCustom.getSubCatList(rootCatId);
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {

        Map<String, Object> map = new HashMap<>();
        map.put("rootCatId", rootCatId);

        return categoryMapperCustom.getSixNewItemsLazy(map);
    }
}
