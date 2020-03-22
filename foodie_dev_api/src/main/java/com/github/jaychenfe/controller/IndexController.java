package com.github.jaychenfe.controller;

import com.github.jaychenfe.enmus.YesOrNo;
import com.github.jaychenfe.pojo.Carousel;
import com.github.jaychenfe.service.CarouselService;
import com.github.jaychenfe.utils.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public IndexController(CarouselService carouselService) {
        this.carouselService = carouselService;
    }

    @ApiOperation(value = "轮播图", notes = "轮播图")
    @GetMapping("/carousel")
    public ApiResponse carousel() {
        List<Carousel> list = carouselService.queryAll(YesOrNo.YES.type);
        return ApiResponse.ok(list);
    }
}
