package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;

    //定义一个常量
    private static final String STATUS = "status";


    @GetMapping("/status")
    public Result<Integer> getStatus(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer statusValue = (Integer) valueOperations.get(STATUS);
        return Result.success(statusValue);
    }

}
