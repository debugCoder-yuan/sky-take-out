package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersPageQueryVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping ("/user/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }
    


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("分页查询订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrdersPageQueryVO> orderDetail(@PathVariable Long id) {
        log.info("查询订单详情：{}", id);
        OrdersPageQueryVO ordersPageQueryVO = orderService.getOrdersDetail(id);
        return Result.success(ordersPageQueryVO);
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        log.info("取消订单：{}", id);
        orderService.cancel(id);
        return Result.success();
    }

}
