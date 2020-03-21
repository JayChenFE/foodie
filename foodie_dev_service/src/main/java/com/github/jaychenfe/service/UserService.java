package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.UserBO;

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
     * @param userBO userBO
     * @return 新创建的用户
     */
    Users createUser(UserBO userBO);
}
