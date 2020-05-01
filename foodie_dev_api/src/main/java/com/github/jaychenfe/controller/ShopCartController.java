package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.bo.ShopCartBO;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jaychenfe
 */
@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopCartController {

    private final RedisOperator redisOperator;

    @Autowired
    public ShopCartController(RedisOperator redisOperator) {
        this.redisOperator = redisOperator;
    }

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

        //  前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        //  如果存在则取出来加上当前数量

        final String shopCartKey = "shopCart:" + userId;
        String shopCartStr = redisOperator.get(shopCartKey);
        List<ShopCartBO> shopCartBOList;
        if (StringUtils.isNotBlank(shopCartStr)) {
            shopCartBOList = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);
            boolean contained = false;

            for (ShopCartBO existBO : shopCartBOList) {
                if (existBO.getSpecId().equals(shopcartBO.getSpecId())) {
                    existBO.setBuyCounts(existBO.getBuyCounts() + shopcartBO.getBuyCounts());
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                shopCartBOList.add(shopcartBO);
            }

        } else {
            shopCartBOList = new ArrayList<>();
            shopCartBOList.add(shopcartBO);
        }

        // 覆盖缓存
        redisOperator.set(shopCartKey, JsonUtils.objectToJson(shopCartBOList));
        return ApiResponse.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品")
    @PostMapping("/del")
    public ApiResponse del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return ApiResponse.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除后端redis购物车中的商品

        final String shopCartKey = "shopCart:" + userId;
        String shopCartStr = redisOperator.get(shopCartKey);
        List<ShopCartBO> shopCartBOList;
        if (StringUtils.isNotBlank(shopCartStr)) {
            shopCartBOList = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);

            for (ShopCartBO existBO : shopCartBOList) {
                if (existBO.getSpecId().equals(itemSpecId)) {
                    shopCartBOList.remove(existBO);
                    break;
                }
            }
            // 覆盖缓存
            redisOperator.set(shopCartKey, JsonUtils.objectToJson(shopCartBOList));
        }

        return ApiResponse.ok();
    }
}
