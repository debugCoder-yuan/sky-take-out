package com.sky.mapper;


import com.sky.dto.GoodsSalesDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface ReportMapper {

    /**
     * 根据动态条件统计营业额
     * @param map
     * @return
     */
    Double turnover(Map map);

    Integer countUser(Map map);

    Integer countOrder(Map map);

    List<GoodsSalesDTO> top10(Map map);
}