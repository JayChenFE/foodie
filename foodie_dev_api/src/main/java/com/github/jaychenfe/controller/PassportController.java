package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaychenfe
 */
@RestController
@RequestMapping("passport")
public class PassportController {

    private static final int MIN_LENGTH_OF_PASSWORD = 6;

    private UserService userService;

    @Autowired
    public PassportController(UserService userService) {
        this.userService = userService;
    }

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

}
