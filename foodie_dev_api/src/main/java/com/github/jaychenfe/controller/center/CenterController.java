package com.github.jaychenfe.controller.center;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.service.center.CenterUserService;
import com.github.jaychenfe.utils.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaychenfe
 */
@Api(value = "center - 用户中心", tags = {"用户中心展示的相关接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    private CenterUserService centerUserService;

    @Autowired
    public CenterController(CenterUserService centerUserService) {
        this.centerUserService = centerUserService;
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "GET")
    @GetMapping("userInfo")
    public ApiResponse userInfo(@ApiParam(name = "userId", value = "用户id", required = true)
                                @RequestParam String userId) {

        Users user = centerUserService.queryUserInfo(userId);
        return ApiResponse.ok(user);
    }


}
