package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.R;
import com.example.dto.DishDto;
import com.example.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表： dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //更新菜品，及其口味，也是更新两个表
    public void upDataByIdWithFlavor(DishDto dishDto);

    //删除菜品
    void deleteWithFlavor(List<Long> id);

    //设置菜品状态
     void updataStatus(int status, List<Long> ids);
}
