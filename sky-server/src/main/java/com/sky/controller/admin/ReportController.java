package com.sky.controller.admin;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnOverStatistics(DataOverViewQueryDTO dataOverViewQueryDTO){
        TurnoverReportVO turnoverReportVO = reportService.turnOver(dataOverViewQueryDTO);
        return Result.success(turnoverReportVO) ;
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(DataOverViewQueryDTO dataOverViewQueryDTO){
        UserReportVO userReportVO = reportService.userCount(dataOverViewQueryDTO);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(DataOverViewQueryDTO dataOverViewQueryDTO){
        OrderReportVO orderReportVO = reportService.orderCount(dataOverViewQueryDTO);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(DataOverViewQueryDTO dataOverViewQueryDTO){
        SalesTop10ReportVO salesTop10ReportVO = reportService.top10(dataOverViewQueryDTO);
        return Result.success(salesTop10ReportVO);
    }
}
