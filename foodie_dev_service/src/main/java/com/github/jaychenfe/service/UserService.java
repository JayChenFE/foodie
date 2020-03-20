package com.github.jaychenfe.service;

/**
 * @author jaychenfe
 */
public interface UserService {
    /**
     * 判断用户是否存在
     * @param username 用户名
     * @return 是否存在该用户
     */
    boolean queryUsernameIsExist(String username);
}
