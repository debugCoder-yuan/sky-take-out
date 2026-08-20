package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    void update(ShoppingCart existShoppingCart);

    void add(ShoppingCart shoppingCart);

    List<ShoppingCart> list(ShoppingCart currentId);


    void deleteByCondition(ShoppingCart shoppingCart);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
