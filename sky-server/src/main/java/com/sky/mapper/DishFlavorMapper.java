package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    public void insertBatch(List<DishFlavor> flavors);

    void deleteBatchIds(List<Long> ids);

    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishId}")
    void deleteByDishId(Long dishId);
}
