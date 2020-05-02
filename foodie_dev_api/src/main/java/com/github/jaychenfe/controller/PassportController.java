package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.ShopCartBO;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.pojo.vo.UserVO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.CookieUtils;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.Md5Utils;
import com.github.jaychenfe.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(value = "注册登录", tags = {"注册登录"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController{

    private static final int MIN_LENGTH_OF_PASSWORD = 6;

    private final UserService userService;
    private final RedisOperator redisOperator;


    @Autowired
    public PassportController(UserService userService, RedisOperator redisOperator) {
        this.userService = userService;
        this.redisOperator = redisOperator;
    }

    @ApiOperation(value = "用户名否存在", notes = "检查用户名是否存在")
    @GetMapping("/usernameIsExist")
    public ApiResponse usernameIsExist(@RequestParam String username) {
        // 1.判断用户名是否为空
        if (StringUtils.isBlank(username)) {
            return ApiResponse.errorMsg("用户名不能为空");
        }

        // 2.查找用户名是否为存在
        if (userService.queryUsernameIsExist(username)) {
            return ApiResponse.errorMsg("用户已经存在");
        }

        return ApiResponse.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public ApiResponse register(@RequestBody UserBO userBO,
                                HttpServletRequest request,
                                HttpServletResponse response) {


        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 1.判断用户名密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)) {
            return ApiResponse.errorMsg("用户名或密码不能为空");
        }

        // 2.查找用户名是否为存在
        if (userService.queryUsernameIsExist(username)) {
            return ApiResponse.errorMsg("用户已经存在");
        }

        // 3.检查密码长度
        if (password.length() < MIN_LENGTH_OF_PASSWORD) {
            return ApiResponse.errorMsg("密码长度不能少于" + MIN_LENGTH_OF_PASSWORD + "位");
        }

        // 4.检查密码是否一致
        if (!password.equals(confirmPassword)) {
            return ApiResponse.errorMsg("两次输入密码不一致");
        }

        Users user = userService.createUser(userBO);
        // 实现redis用户会话
        UserVO userVO = saveSessionAndConvertUserVO(user);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userVO), true);

        syncShopCartData(userVO.getId(), request, response);

        return ApiResponse.ok(userVO);
    }


    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("/login")
    public ApiResponse login(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestBody UserBO userBO) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 1.判断用户名密码是否为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return ApiResponse.errorMsg("用户名或密码不能为空");
        }

        Users user = userService.queryUserForLogin(username, Md5Utils.getMd5Str(password));
        if (user == null) {
            return ApiResponse.errorMsg("用户名或密码错误");
        }

        UserVO userVO = saveSessionAndConvertUserVO(user);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userVO), true);
        syncShopCartData(userVO.getId(), request, response);
        return ApiResponse.ok(userVO);

    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录")
    @PostMapping("/logout")
    public ApiResponse logout(@RequestParam String userId,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        // 清除redis会话
        redisOperator.del("user_token:" + userId);

        // 清空cookie购物车
        CookieUtils.deleteCookie(request, response, "user");

        return ApiResponse.ok();
    }



    /**
     * 注册登录成功后,同步购物车相关cookie和redis中的数据
     * 1. redis无数据,cookie为空,不处理
     * cookie不为空,直接放入redis
     * <p>
     * 2. redis有数据,cookie为空,使用redis覆盖cookie,
     * cookie不为空,如果cookie中某个商品在redis中存在,
     * 则以cookie为主,删除redis中的(参考京东)
     * <p>
     * 3.同步到redis后,覆盖本地cookie购物车数据
     */

    private void syncShopCartData(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        final String redisKey = "shopCart:" + userId;
        final String cookieName = "shopcart";

        String redisJson = redisOperator.get(redisKey);
        String cookieJson = CookieUtils.getCookieValue(request, cookieName, true);

        if (StringUtils.isBlank(redisJson)) {
            if (!StringUtils.isBlank(cookieJson)) {
                redisOperator.set(redisKey, redisJson);
            }
        } else {
            if (StringUtils.isBlank(cookieJson)) {
                CookieUtils.setCookie(request, response, cookieName, redisJson, true);
            } else {
                /**
                 * 1. 已经存在的,把cookie中对应的数量覆盖redis(参考京东)
                 * 2. 该商品标记为待删除,统一放入一个待删除的list
                 * 3. 从cookie清除待删除list
                 * 4. 合并redis和cookie数据
                 * 5. 使用合并后的数据更新redis和cookie
                 */
                List<ShopCartBO> redisShopCartBOList = JsonUtils.jsonToList(redisJson, ShopCartBO.class);
                List<ShopCartBO> cookieShopCartBOList = JsonUtils.jsonToList(cookieJson, ShopCartBO.class);
                List<ShopCartBO> toDeleteShopCartBOList = new ArrayList<>();

                for (ShopCartBO redisBO : redisShopCartBOList) {
                    for (ShopCartBO cookieBo : cookieShopCartBOList) {
                        if (redisBO.getSpecId().equals(cookieBo.getSpecId())) {
                            redisBO.setBuyCounts(cookieBo.getBuyCounts());
                            toDeleteShopCartBOList.add(cookieBo);
                        }
                    }
                }

                cookieShopCartBOList.removeAll(toDeleteShopCartBOList);
                redisShopCartBOList.addAll(cookieShopCartBOList);

                String shopCartJson = JsonUtils.objectToJson(redisShopCartBOList);
                redisOperator.set(redisKey, shopCartJson);
                CookieUtils.setCookie(request, response, cookieName, shopCartJson, true);
            }
        }
    }
}
