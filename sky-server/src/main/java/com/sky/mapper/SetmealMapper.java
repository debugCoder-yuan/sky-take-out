package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal (name, category_id, price, description, image) " +
            "values (#{name}, #{categoryId}, #{price}, #{description}, #{image})")
    void save(Setmeal setmeal);

    Page<Setmeal> page(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    void delete(List<Long> ids);

    List<Long> getOnSaleSetmealIds(List<Long> ids);

    @Update("update setmeal set name = #{name}, category_id = #{categoryId}, price = #{price}, description = #{description}, image = #{image} where id = #{id}")
    Setmeal updateDTO(Setmeal setmeal);
}