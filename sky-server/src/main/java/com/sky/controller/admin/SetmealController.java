package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j

public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", key = "#id")
    public Result updateStatus(@PathVariable Integer status, Long id){
        setmealService.updateStatus(status, id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> get(@PathVariable Long id){
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }

    @PutMapping
    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }
}
