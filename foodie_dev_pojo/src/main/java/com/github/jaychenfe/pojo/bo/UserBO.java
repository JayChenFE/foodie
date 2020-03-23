package com.github.jaychenfe.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author jaychenfe
 */
@ApiModel(value = "用户对象BO", description = "从客户端,由用户传入的数据封装")
public class UserBO {

    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String toString() {
        return "UserBO(username=" + this.getUsername() + ", password=" + this.getPassword() + ", confirmPassword=" + this.getConfirmPassword() + ")";
    }
}
