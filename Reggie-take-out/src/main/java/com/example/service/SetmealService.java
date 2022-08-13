package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.DishDto;
import com.example.dto.SetmealDto;
import com.example.entity.Setmeal;

import java.util.List;

public interface SetmealService  extends IService<Setmeal> {

    //分页查询套餐信息
    public IPage getPage(int currentPage, int pageSize,String name);


    //添加套餐，即器套餐包含的菜品
    public void saveWithDish(SetmealDto setmealDto);

    //查找套餐，用于修改页面的回显
    public SetmealDto setmealWithDish(Long id);


    //更新套餐，及其包含菜品，也是更新两个表
    public void upDataSetmealWithDish(SetmealDto setmealDto);

    //设置菜品状态
    void setmealUpdataStatus(int status, List<Long> ids);

    //删除套餐
    void deleteWithSetmeal(List<Long> ids);

    //根据种类查找套餐
    List getSetmealByCategory(Long id,int status);

}
