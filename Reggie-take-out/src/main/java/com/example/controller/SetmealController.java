package com.example.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.R;
import com.example.dto.SetmealDto;
import com.example.entity.Setmeal;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 分页查询套餐信息
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public R getPage(int page, int pageSize, String name) {


        log.info("查询页数为:{},查询条数为:{}", page, pageSize);

        //调用查询方法
        IPage Ipage = setmealService.getPage(page, pageSize, name);
        //如果删除后，查询页数大于可查的最大页数，则查询最大页数
        if (page > Ipage.getPages()) {
            Ipage = setmealService.getPage((int) Ipage.getPages(), pageSize, name);
        }


        return R.success(Ipage);
    }


    /**
     * 添加套餐信息即其菜品信息
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody SetmealDto setmealDto) {

        log.info(setmealDto.toString());

        setmealService.saveWithDish(setmealDto);

        return R.success("添加套餐成功");
    }


    /***
     * 查询信息，用于修改界面的信息回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {

        SetmealDto setmealDto = setmealService.setmealWithDish(id);


        return R.success(setmealDto);
    }


    /**
     * 更新套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R update(@RequestBody SetmealDto setmealDto) {

        setmealService.upDataSetmealWithDish(setmealDto);
        return R.success("修改套餐信息成功");

    }


    /**
     * 更新套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R updataStatus(@PathVariable int status, @RequestParam List<Long> ids) {
        log.info("要修改套餐的状态为：{}，套餐id为：{}", status, ids);
        setmealService.setmealUpdataStatus(status,ids);

        return R.success("修改套餐状态成功");

    }

    /**
     *
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R deleteById(@RequestParam List<Long> ids) {
        log.info(ids.toString());

        //调用方法，删除
        setmealService.deleteWithSetmeal(ids);

        return R.success("删除套餐成功");
    }


/*
 */
    @GetMapping("/list")
    public R  getSetmealByCategory(Long categoryId,int status){

        List list= setmealService.getSetmealByCategory(categoryId, status);

        return  R.success(list);
    }


}