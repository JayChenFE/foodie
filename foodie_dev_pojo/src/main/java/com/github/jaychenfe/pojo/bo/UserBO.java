package com.github.jaychenfe.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jaychenfe
 */
@Data
@ApiModel(value = "用户对象BO", description = "从客户端,由用户传入的数据封装")
public class UserBO {

    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;
}
