package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.properties.BaiduProperties;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.BaiduMapUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Autowired
    private BaiduMapUtil baiduMapUtil;

    @Autowired
    private BaiduProperties baiduProperties;
    @Override

    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {


        //地址簿为空
        Long addressBookId = ordersSubmitDTO.getAddressBookId();
        AddressBook addressBook = addressBookMapper.getById(addressBookId);
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车为空
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(addressBook.getProvinceName());
        sb.append(addressBook.getCityName());
        sb.append(addressBook.getDistrictName());
        sb.append(addressBook.getDetail());
        Integer distance = baiduMapUtil.calculateDistance(baiduProperties.getShop_address(), sb.toString());
        log.info("距离：{}", distance);
        if (distance > 5000) {
            throw new OrderBusinessException(MessageConstant.TOO_FARAWAY);
        }
        //插入订单表
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setOrderTime(LocalDateTime.now());

        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orderMapper.insert(orders);
        //插入订单明细表
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart sc : list){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(sc, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.batchInsert(orderDetails);
        //清空购物车
        shoppingCartMapper.deleteByCondition(shoppingCart);
        //封装返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }

    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;

        log.info("跳过微信支付，直接生成支付结果");
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return new OrderPaymentVO();
    }

    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrdersPageQueryVO> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrdersPageQueryVO> result = page.getResult();
        for(OrdersPageQueryVO vo : result){
            Long id = vo.getId();
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
            vo.setOrderDetailList(orderDetails);

        }


        return new PageResult(page.getTotal(), result);
    }

    @Override
    public OrdersPageQueryVO getOrdersDetail(Long id) {

        OrdersPageQueryVO ordersPageQueryVO = orderMapper.getById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        ordersPageQueryVO.setOrderDetailList(orderDetails);
        return ordersPageQueryVO;
    }

    @Transactional
    @Override
    public void repetition(Long id) {

        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);


    }

    @Transactional
    @Override
    public void cancel(Long id) {
        Orders ordersDB = orderMapper.getByIdOrders(id);
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            //当订单状态大于2时，不可取消订单
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            //  weChatPayUtil.refund(
            //          ordersDB.getNumber(), //商户订单号
            //          ordersDB.getNumber(), //商户退款单号
            //          new BigDecimal(0.01),//退款金额，单位 元
            //          new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);

    }

    @Override
    public PageResult adminPageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrdersPageQueryVO> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrdersPageQueryVO> result = page.getResult();
        for (OrdersPageQueryVO vo : result) {
            Long id = vo.getId();
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
            String orderDishes = orderDetails.stream().map(x -> x.getName() + " x" + x.getNumber()).collect(Collectors.joining("，"));
            vo.setOrderDishes(orderDishes);
//            vo.setOrderDetailList(orderDetails);
        }

        return new PageResult(page.getTotal(),result);
    }

    @Override
    public OrderStatisticsVO getStatistics() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.getStatistics();
        return orderStatisticsVO;
    }

    @Override
    public OrderVO getOrderDetailById(Long id) {
        OrderVO orderVO = orderMapper.getOrderDetailById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        StringBuilder sb = new StringBuilder();
        for (OrderDetail orderDetail : orderDetails) {
            sb.append(orderDetail.getName()).append(" x").append(orderDetail.getNumber()).append("，");
        }
        orderVO.setOrderDishes(sb.toString());
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        orderMapper.updateStatus(ordersConfirmDTO.getId(), ordersConfirmDTO.getStatus());
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        orderMapper.rejection(ordersRejectionDTO.getId(), ordersRejectionDTO.getRejectionReason());
    }

    @Override
    public void adminCancel(OrdersCancelDTO ordersCancelDTO) {
        Orders ordersDB = orderMapper.getByIdOrders(ordersCancelDTO.getId());
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            //当订单状态大于2时，不可取消订单
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            //  weChatPayUtil.refund(
            //          ordersDB.getNumber(), //商户订单号
            //          ordersDB.getNumber(), //商户退款单号
            //          new BigDecimal(0.01),//退款金额，单位 元
            //          new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    @Override
    public void complete(Long id) {
        Orders orders = orderMapper.getByIdOrders(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }

    @Override
    public void delivery(Long id) {
        Orders orders = orderMapper.getByIdOrders(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }


}
