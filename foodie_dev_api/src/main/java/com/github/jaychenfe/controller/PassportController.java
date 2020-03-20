package com.github.jaychenfe.controller;

import com.github.jaychenfe.service.UserService;
import com.github.jaychenfe.utils.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaychenfe
 */
@RestController
@RequestMapping("passport")
public class PassportController {

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

}
