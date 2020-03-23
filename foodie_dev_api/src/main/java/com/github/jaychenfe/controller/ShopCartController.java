package com.github.jaychenfe.controller;


import com.github.jaychenfe.pojo.bo.ShopCartBO;
import com.github.jaychenfe.utils.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jaychenfe
 */
@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopCartController {

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车")
    @PostMapping("/add")
    public ApiResponse add(
            @RequestParam String userId,
            @RequestBody ShopCartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg("");
        }

        System.out.println(shopcartBO);

        // TODO 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存

        return ApiResponse.ok();
    }
}
