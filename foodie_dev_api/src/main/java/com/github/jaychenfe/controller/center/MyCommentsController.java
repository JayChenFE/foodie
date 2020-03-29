package com.github.jaychenfe.controller.center;

import com.github.jaychenfe.controller.BaseController;
import com.github.jaychenfe.enmus.YesOrNo;
import com.github.jaychenfe.pojo.OrderItems;
import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.bo.center.OrderItemsCommentBO;
import com.github.jaychenfe.pojo.vo.MyCommentVO;
import com.github.jaychenfe.service.MyCommentsService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jaychenfe
 */
@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    private MyCommentsService myCommentsService;

    @Autowired
    public MyCommentsController(MyCommentsService myCommentsService) {
        this.myCommentsService = myCommentsService;
    }

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public ApiResponse pending(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        // 判断用户和订单是否关联
        ApiResponse checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断该笔订单是否已经评价过，评价过了就不再继续
        Orders myOrder = (Orders) checkResult.getData();
        if (myOrder.getIsComment().equals(YesOrNo.YES.type)) {
            return ApiResponse.errorMsg("该笔订单已经评价");
        }

        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);

        return ApiResponse.ok(list);
    }


    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public ApiResponse saveList(@ApiParam(name = "userId", value = "用户id", required = true)
                                @RequestParam String userId,
                                @ApiParam(name = "orderId", value = "订单id", required = true)
                                @RequestParam String orderId,
                                @RequestBody List<OrderItemsCommentBO> commentList) {

        System.out.println(commentList);

        // 判断用户和订单是否关联
        ApiResponse checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断评论内容list不能为空
        if (commentList == null || commentList.isEmpty()) {
            return ApiResponse.errorMsg("评论内容不能为空！");
        }

        myCommentsService.saveComments(orderId, userId, commentList);
        return ApiResponse.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public ApiResponse query(@ApiParam(name = "userId", value = "用户id", required = true)
                             @RequestParam String userId,
                             @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
                             @RequestParam(defaultValue = "1") Integer page,
                             @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
                             @RequestParam(defaultValue = "10") Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return ApiResponse.errorMsg(null);
        }

        PagedGridResult<MyCommentVO> grid = myCommentsService.queryMyComments(userId, page, pageSize);

        return ApiResponse.ok(grid);
    }

}
