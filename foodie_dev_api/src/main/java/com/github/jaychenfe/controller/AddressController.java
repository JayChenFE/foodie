package com.github.jaychenfe.controller;

import com.github.jaychenfe.pojo.UserAddress;
import com.github.jaychenfe.pojo.bo.AddressBO;
import com.github.jaychenfe.service.AddressService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "地址相关", tags = {"地址相关的api接口"})
@RequestMapping("address")
@RestController
public class AddressController {

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1. 查询用户的所有收货地址列表
     * 2. 新增收货地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认地址
     */

    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表")
    @PostMapping("/list")
    public ApiResponse list(
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg("");
        }

        List<UserAddress> list = addressService.queryAll(userId);
        return ApiResponse.ok(list);
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址")
    @PostMapping("/add")
    public ApiResponse add(@RequestBody AddressBO addressBO) {

        ApiResponse checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.addNewUserAddress(addressBO);

        return ApiResponse.ok();
    }

    private ApiResponse checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return ApiResponse.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return ApiResponse.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return ApiResponse.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return ApiResponse.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return ApiResponse.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return ApiResponse.errorMsg("收货地址信息不能为空");
        }

        return ApiResponse.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址")
    @PostMapping("/update")
    public ApiResponse update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return ApiResponse.errorMsg("修改地址错误：addressId不能为空");
        }

        ApiResponse checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.updateUserAddress(addressBO);

        return ApiResponse.ok();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址")
    @PostMapping("/delete")
    public ApiResponse delete(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ApiResponse.errorMsg("");
        }

        addressService.deleteUserAddress(userId, addressId);
        return ApiResponse.ok();
    }

    @ApiOperation(value = "用户设置默认地址", notes = "用户设置默认地址")
    @PostMapping("/setDefalut")
    public ApiResponse setDefalut(
            @RequestParam String userId,
            @RequestParam String addressId) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ApiResponse.errorMsg("");
        }

        addressService.updateUserAddressToBeDefault(userId, addressId);
        return ApiResponse.ok();
    }
}
