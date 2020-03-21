package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.bo.UserBO;
import com.github.jaychenfe.pojo.vo.UserVO;

import java.util.Optional;

/**
 * @author jaychenfe
 */
public interface UserService {
    /**
     * 判断用户是否存在
     *
     * @param username 用户名
     * @return 是否存在该用户
     */
    boolean queryUsernameIsExist(String username);


    /**
     * 创建用户
     *
     * @param userBO userBO
     * @return 新创建的用户
     */
    UserVO createUser(UserBO userBO);

    /**
     * 检查用户名和密码是否匹配,用于登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    Optional<UserVO> queryUserForLogin(String username, String password);
}

