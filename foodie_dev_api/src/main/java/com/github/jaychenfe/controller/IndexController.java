package com.github.jaychenfe.controller;

import com.github.jaychenfe.enmus.YesOrNo;
import com.github.jaychenfe.pojo.Carousel;
import com.github.jaychenfe.pojo.Category;
import com.github.jaychenfe.pojo.vo.CategoryVO;
import com.github.jaychenfe.pojo.vo.NewItemsVO;
import com.github.jaychenfe.service.CarouselService;
import com.github.jaychenfe.service.CategoryService;
import com.github.jaychenfe.utils.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jaychenfe
 */
@Api(value = "首页", tags = {"首页"})
@RestController
@RequestMapping("index")
public class IndexController {

    private CarouselService carouselService;
    private CategoryService categoryService;

    @Autowired
    public IndexController(CarouselService carouselService, CategoryService categoryService) {
        this.carouselService = carouselService;
        this.categoryService = categoryService;
    }

    @ApiOperation(value = "轮播图", notes = "轮播图")
    @GetMapping("/carousel")
    public ApiResponse carousel() {
        List<Carousel> list = carouselService.queryAll(YesOrNo.YES.type);
        return ApiResponse.ok(list);
    }


    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     *
     * @return ApiResponse
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public ApiResponse cats() {
        List<Category> list = categoryService.queryAllRootLevelCat();
        return ApiResponse.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类")
    @GetMapping("/subCat/{rootCatId}")
    public ApiResponse subCat(@ApiParam(name = "rootCatId", value = "一级分类id", required = true)
                              @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ApiResponse.errorMsg("分类不存在");
        }

        List<CategoryVO> list = categoryService.getSubCatList(rootCatId);
        return ApiResponse.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据")
    @GetMapping("/sixNewItems/{rootCatId}")
    public ApiResponse sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return ApiResponse.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return ApiResponse.ok(list);
    }

}