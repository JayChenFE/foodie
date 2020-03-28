package com.github.jaychenfe.service;

import com.github.jaychenfe.pojo.UserAddress;
import com.github.jaychenfe.pojo.bo.AddressBO;

import java.util.List;

/**
 * @author jaychenfe
 */
public interface AddressService {

    /**
     * 根据用户id查询用户的收货地址列表
     *
     * @param userId 用户id
     * @return 用户的收货地址列表
     */
    List<UserAddress> queryAll(String userId);

    /**
     * 用户新增地址
     *
     * @param addressBO addressBO
     */
    void addNewUserAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     *
     * @param addressBO addressBO
     */
    void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用户id和地址id，删除对应的用户地址信息
     *
     * @param userId    用户id
     * @param addressId 地址id
     */
    void deleteUserAddress(String userId, String addressId);

    /**
     * 修改默认地址
     *
     * @param userId    用户id
     * @param addressId 地址id
     */
    void updateUserAddressToBeDefault(String userId, String addressId);

    /**
     * 根据用户id和地址id，查询具体的用户地址对象信息
     *
     * @param userId    用户id
     * @param addressId 地址id
     * @return 地址
     */
    UserAddress queryUserAddress(String userId, String addressId);
}
