package com.github.jaychenfe.controller;

import com.github.jaychenfe.enmus.OrderStatusEnum;
import com.github.jaychenfe.enmus.PayMethod;
import com.github.jaychenfe.pojo.OrderStatus;
import com.github.jaychenfe.pojo.bo.ShopCartBO;
import com.github.jaychenfe.pojo.bo.SubmitOrderBO;
import com.github.jaychenfe.pojo.vo.MerchantOrdersVO;
import com.github.jaychenfe.pojo.vo.OrderVO;
import com.github.jaychenfe.service.OrderService;
import com.github.jaychenfe.utils.ApiResponse;
import com.github.jaychenfe.utils.CookieUtils;
import com.github.jaychenfe.utils.JsonUtils;
import com.github.jaychenfe.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author jaychenfe
 */
@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private final OrderService orderService;
    private final RestTemplate restTemplate;
    private final RedisOperator redisOperator;

    @Autowired
    public OrdersController(OrderService orderService, RestTemplate restTemplate, RedisOperator redisOperator) {
        this.orderService = orderService;
        this.restTemplate = restTemplate;
        this.redisOperator = redisOperator;
    }


    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public ApiResponse create(@RequestBody SubmitOrderBO submitOrderBO,
                              HttpServletRequest request,
                              HttpServletResponse response) {

        boolean isSupportedPayMethod = Stream.of(PayMethod.values())
                .anyMatch(x -> x.type.equals(submitOrderBO.getPayMethod()));
        if (!isSupportedPayMethod) {
            return ApiResponse.errorMsg("支付方式不支持！");
        }

        // 从redis获取购物车数据
        final String shopCartKey = "shopCart:" + submitOrderBO.getUserId();
        String shopCartJson = redisOperator.get(shopCartKey);

        if (StringUtils.isBlank(shopCartJson)) {
            return ApiResponse.errorMsg("购物车数据不正确");
        }

        List<ShopCartBO> shopCartBOList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);

        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(submitOrderBO, shopCartBOList);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        // 删除redis购物车中的商品,并同步到cookie
        shopCartBOList.removeAll(orderVO.getToRemoveShopCartBOList());
        String newShopCartStr = JsonUtils.objectToJson(shopCartBOList);
        redisOperator.set(shopCartKey, newShopCartStr);
        CookieUtils.setCookie(request, response, "shopcart", newShopCartStr, true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        ApiResponse paymentResult = sendMerchantOrders(orderVO);
        assert paymentResult != null;
        if (!paymentResult.isOk()) {
            logger.error("发送错误：{}", paymentResult.getMsg());
            return ApiResponse.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        return ApiResponse.ok(orderId);
    }

    private ApiResponse sendMerchantOrders(OrderVO orderVO) {
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity =
                new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<ApiResponse> responseEntity =
                restTemplate.postForEntity(paymentUrl, entity, ApiResponse.class);
        return responseEntity.getBody();
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public ApiResponse getPaidOrderInfo(String orderId) {

        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return ApiResponse.ok(orderStatus);
    }

}
