package com.github.jaychenfe.service.impl;


import com.github.jaychenfe.enmus.OrderStatusEnum;
import com.github.jaychenfe.enmus.YesOrNo;
import com.github.jaychenfe.mapper.OrderItemsMapper;
import com.github.jaychenfe.mapper.OrderStatusMapper;
import com.github.jaychenfe.mapper.OrdersMapper;
import com.github.jaychenfe.pojo.Items;
import com.github.jaychenfe.pojo.ItemsSpec;
import com.github.jaychenfe.pojo.OrderItems;
import com.github.jaychenfe.pojo.OrderStatus;
import com.github.jaychenfe.pojo.Orders;
import com.github.jaychenfe.pojo.UserAddress;
import com.github.jaychenfe.pojo.bo.SubmitOrderBO;
import com.github.jaychenfe.pojo.vo.MerchantOrdersVO;
import com.github.jaychenfe.pojo.vo.OrderVO;
import com.github.jaychenfe.service.AddressService;
import com.github.jaychenfe.service.ItemService;
import com.github.jaychenfe.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    private OrdersMapper ordersMapper;
    private OrderItemsMapper orderItemsMapper;
    private OrderStatusMapper orderStatusMapper;
    private AddressService addressService;
    private ItemService itemService;
    private Sid sid;

    @Autowired
    public OrderServiceImpl(OrdersMapper ordersMapper,
                            OrderItemsMapper orderItemsMapper,
                            OrderStatusMapper orderStatusMapper,
                            AddressService addressService,
                            ItemService itemService,
                            Sid sid) {
        this.ordersMapper = ordersMapper;
        this.orderItemsMapper = orderItemsMapper;
        this.orderStatusMapper = orderStatusMapper;
        this.addressService = addressService;
        this.itemService = itemService;
        this.sid = sid;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBO) {

        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        // 包邮费用设置为0
        int postAmount = 0;

        String orderId = sid.nextShort();
        UserAddress address = addressService.queryUserAddress(userId, addressId);

        // 1. 新订单数据
        Orders newOrder = createNewOrder(submitOrderBO, postAmount, orderId, address);

        // 2. 循环根据itemSpecIds保存订单商品信息表,并计算实际支付价格
        int realPayAmount = saveNewOrderAndGetRealPayAmount(itemSpecIds, orderId, newOrder);

        // 3. 保存订单状态表
        saveOrderStatus(orderId);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = createMerchantOrdersVO(userId, payMethod, postAmount, orderId, realPayAmount);

        // 5. 构建自定义订单vo
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);

        return orderVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    private MerchantOrdersVO createMerchantOrdersVO(String userId, Integer payMethod, int postAmount, String orderId, int realPayAmount) {
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);
        return merchantOrdersVO;
    }

    private void saveOrderStatus(String orderId) {
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);
    }

    private int saveNewOrderAndGetRealPayAmount(String itemSpecIds, String orderId, Orders newOrder) {
        String[] itemSpecIdArr = itemSpecIds.split(",");
        // 商品原价累计
        int totalAmount = 0;
        // 优惠后的实际支付价格累计
        int realPayAmount = 0;
        for (String itemSpecId : itemSpecIdArr) {

            // TODO 整合redis后，商品购买的数量重新从redis的购物车中获取
            int buyCounts = 1;

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemSpec.getPriceDiscount() * buyCounts;

            // 2.2 根据商品id，获得商品信息以及商品图片
            String itemId = itemSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            // 2.3 循环保存订单商品数据到数据库
            String orderItemsId = sid.nextShort();
            OrderItems orderItems = new OrderItems();
            orderItems.setId(orderItemsId);
            orderItems.setOrderId(orderId);
            orderItems.setItemId(itemId);
            orderItems.setItemName(item.getItemName());
            orderItems.setItemImg(imgUrl);
            orderItems.setBuyCounts(buyCounts);
            orderItems.setItemSpecId(itemSpecId);
            orderItems.setItemSpecName(itemSpec.getName());
            orderItems.setPrice(itemSpec.getPriceDiscount());
            orderItemsMapper.insert(orderItems);

            // 2.4 在用户提交订单以后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);
        return realPayAmount;
    }

    private Orders createNewOrder(SubmitOrderBO submitOrderBO, Integer postAmount, String orderId, UserAddress address) {
        Orders newOrder = new Orders();
        BeanUtils.copyProperties(submitOrderBO, newOrder);
        newOrder.setId(orderId);
        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(getReceiverAddress(address));
        newOrder.setPostAmount(postAmount);
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());
        return newOrder;
    }

    private String getReceiverAddress(UserAddress address) {
        return address.getProvince() + " "
                + address.getCity() + " "
                + address.getDistrict() + " "
                + address.getDetail();
    }
}
