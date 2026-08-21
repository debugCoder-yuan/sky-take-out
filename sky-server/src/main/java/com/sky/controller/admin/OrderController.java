package com.sky.controller.admin;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import com.sky.vo.OrdersPageQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@Slf4j
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @GetMapping("conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.adminPageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.getStatistics();
        return Result.success(orderStatisticsVO);
    }

    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetailById(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetailById(id);
        return Result.success(orderVO);
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }

}


