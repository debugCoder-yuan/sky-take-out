package com.sky.service.impl;


import com.sky.dto.DataOverViewQueryDTO;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;

import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportServiceImpl  implements ReportService {


    @Autowired
    private ReportMapper reportMapper;
    @Override
    public TurnoverReportVO turnOver(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDate begin = dataOverViewQueryDTO.getBegin();
        LocalDate end = dataOverViewQueryDTO.getEnd();
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> dataList = new ArrayList<>();
        for(LocalDate date : dateList){
            Map map = new HashMap();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            map.put("status", Orders.COMPLETED);
            Double sumValue = reportMapper.turnover(map) == null ? 0 : reportMapper.turnover(map);
            dataList.add(sumValue);
        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(dataList,","))
                .build();

    }

    @Override
    public UserReportVO userCount(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDate begin = dataOverViewQueryDTO.getBegin();
        LocalDate end = dataOverViewQueryDTO.getEnd();
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> oldUserList = new ArrayList<>();
        for(LocalDate date : dateList){
            Map map = new HashMap();
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalSum = reportMapper.countUser(map);
            oldUserList.add(totalSum);
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            Integer newSum = reportMapper.countUser(map);
            newUserList.add(newSum);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(oldUserList,","))
                .build();

    }

    @Override
    public OrderReportVO orderCount(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDate begin = dataOverViewQueryDTO.getBegin();
        LocalDate end = dataOverViewQueryDTO.getEnd();
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        double orderCompletionRate = 0.0;
        for(LocalDate date : dateList){
            Map map = new HashMap();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            Integer totalOrdersNum = reportMapper.countOrder(map);
            totalOrderCount += totalOrdersNum;
            orderCountList.add(totalOrdersNum);
            map.put("status", Orders.COMPLETED);
            Integer validNum  = reportMapper.countOrder(map);
            validOrderCountList.add(validNum);
            validOrderCount += validNum;
        }
        if(totalOrderCount != 0){}
        orderCompletionRate = validOrderCount*1.0/totalOrderCount;

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10(DataOverViewQueryDTO dataOverViewQueryDTO) {
        LocalDate begin = dataOverViewQueryDTO.getBegin();
        LocalDate end = dataOverViewQueryDTO.getEnd();
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        Map map = new HashMap();
        map.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        List<GoodsSalesDTO> goodsSalesDTO = reportMapper.top10(map);
        for(GoodsSalesDTO goodsSalesDTO1 : goodsSalesDTO){
            nameList.add(goodsSalesDTO1.getName());
            numberList.add(goodsSalesDTO1.getNumber());
        }
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

}
