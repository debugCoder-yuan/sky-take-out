package com.sky.service;

import com.sky.dto.DataOverViewQueryDTO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

public interface ReportService {

    TurnoverReportVO turnOver(DataOverViewQueryDTO dataOverViewQueryDTO);

    UserReportVO userCount(DataOverViewQueryDTO dataOverViewQueryDTO);

    OrderReportVO orderCount(DataOverViewQueryDTO dataOverViewQueryDTO);

    SalesTop10ReportVO top10(DataOverViewQueryDTO dataOverViewQueryDTO);
}
