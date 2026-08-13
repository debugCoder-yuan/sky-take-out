package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getByIds(List<Long> ids);

    void saveSetmealDishes(List<SetmealDish> setmealDishes);

    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{id}")
    void deleteBySetmealId(Long id);
}