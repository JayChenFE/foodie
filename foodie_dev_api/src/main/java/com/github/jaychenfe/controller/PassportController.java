package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.CookieUtils;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.Md5Utils;
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

/**
 * @author jaychenfe
 */
@Api(value = "注册登录", tags = {"注册登录"})
@RestController
@RequestMapping("passport")
public class PassportController {

    private static final int MIN_LENGTH_OF_PASSWORD = 6;

    private UserService userService;

    @Autowired
    public PassportController(UserService userService) {
        this.userService = userService;
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
    public ApiResponse register(@RequestBody UserBO userBO) {


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

        return ApiResponse.ok(user);
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

        return userService.queryUserForLogin(username, Md5Utils.getMd5Str(password))
                .map(userVO -> {
                    CookieUtils.setCookie(request, response, "user",
                            JsonUtils.objectToJson(userVO), true);
                    return ApiResponse.ok(userVO);
                })
                .orElse(ApiResponse.errorMsg("用户名或密码错误"));
    }

}
