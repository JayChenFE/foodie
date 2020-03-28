package com.github.jaychenfe.controller.center;

import com.github.jaychenfe.controller.BaseController;
import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.vo.OrderStatusCountsVO;
import com.github.jaychenfe.service.center.MyOrdersService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaychenfe
 */
@Api(value = "用户中心我的订单", tags = {"用户中心我的订单相关接口"})
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {

    private MyOrdersService myOrdersService;

    @Autowired
    public MyOrdersController(MyOrdersService myOrdersService) {
        this.myOrdersService = myOrdersService;
    }

    @ApiOperation(value = "获得订单状态数概况", notes = "获得订单状态数概况", httpMethod = "POST")
    @PostMapping("/statusCounts")
    public ApiResponse statusCounts(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg(null);
        }

        OrderStatusCountsVO result = myOrdersService.getOrderStatusCounts(userId);

        return ApiResponse.ok(result);
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public ApiResponse query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态")
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询下一页的第几页")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.queryMyOrders(userId,
                orderStatus,
                page,
                pageSize);

        return ApiResponse.ok(grid);
    }


    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public ApiResponse deliver(@ApiParam(name = "orderId", value = "订单id", required = true)
                               @RequestParam String orderId) {

        // 商家发货没有后端，所以这个接口仅仅只是用于模拟
        if (StringUtils.isBlank(orderId)) {
            return ApiResponse.errorMsg("订单ID不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return ApiResponse.ok();
    }


    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public ApiResponse confirmReceive(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        ApiResponse checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return ApiResponse.errorMsg("订单确认收货失败！");
        }

        return ApiResponse.ok();
    }

    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public ApiResponse delete(@ApiParam(name = "orderId", value = "订单id", required = true)
                              @RequestParam String orderId,
                              @ApiParam(name = "userId", value = "用户id", required = true)
                              @RequestParam String userId) {

        ApiResponse checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if (!res) {
            return ApiResponse.errorMsg("订单删除失败！");
        }

        return ApiResponse.ok();
    }


    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return
     */
    private ApiResponse checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return ApiResponse.errorMsg("订单不存在！");
        }
        return ApiResponse.ok();
    }

    @ApiOperation(value = "查询订单动向", notes = "查询订单动向", httpMethod = "POST")
    @PostMapping("/trend")
    public ApiResponse trend(@ApiParam(name = "userId", value = "用户id", required = true)
                             @RequestParam String userId,
                             @ApiParam(name = "page", value = "查询下一页的第几页")
                             @RequestParam Integer page,
                             @ApiParam(name = "pageSize", value = "分页的每一页显示的条数")
                             @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.getOrdersTrend(userId, page, pageSize);

        return ApiResponse.ok(grid);
    }

}
