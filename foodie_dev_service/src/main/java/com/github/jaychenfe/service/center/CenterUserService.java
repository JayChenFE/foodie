package com.github.jaychenfe.service.center;


import com.github.jaychenfe.pojo.Users;
import com.github.jaychenfe.pojo.bo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     *
     * @param userId       用户id
     * @param centerUserBO centerUserBO
     * @return 用户信息
     */
    Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 用户头像更新
     *
     * @param userId  用户id
     * @param faceUrl 头像
     * @return 用户信息
     */
    Users updateUserFace(String userId, String faceUrl);
}
