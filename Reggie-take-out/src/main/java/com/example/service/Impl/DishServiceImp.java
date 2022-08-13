package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.dto.DishDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.mapper.DishMapper;
import com.example.service.CategoryService;
import com.example.service.DishFlavorService;
import com.example.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class DishServiceImp extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    //分页查询
    public IPage getPage(int currentPage, int pageSize, String name) {

        //  条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //分页构造器
        IPage<Dish> pageInfo = new Page(currentPage, pageSize);
        IPage<DishDto> dishDtoIpage = new Page();

        //添加排序条件
        lqw.orderByDesc(Dish::getUpdateTime);

        //添加过滤条件
        lqw.like(name != null, Dish::getName, name);

        //执行分页查询
        pageInfo = dishMapper.selectPage(pageInfo, lqw);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoIpage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {

            //创建对象
            DishDto dishDto = new DishDto();
            //拷贝其他属性
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类Id
            Category category = categoryService.getById(categoryId);//查到的分类类
            String categoryName = category.getName();//分类名

            //设置分类名属性
            dishDto.setCategoryName(categoryName);

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoIpage.setRecords(list);


        log.info("执行菜品分页查询");
        return dishDtoIpage;

    }

    /**
     * 新增菜品，同时保持口味数据
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品dish
        dishMapper.insert(dishDto);//因为dishDto继承了Dish，所以直接封装

        Long dishId = dishDto.getId();//菜品id

        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味

        flavors.stream().map((item -> {
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(dishDto.getFlavors());

    }

    /**
     * 更新菜品和口味
     * @param dishDto
     */
    @Transactional
    public void upDataByIdWithFlavor(DishDto dishDto){


        //修改菜品的基本信息到菜品dish
        dishMapper.updateById(dishDto);//因为dishDto继承了Dish，所以直接封装

        //清理当前菜品对应的口味，再重新添加
        Long dishId = dishDto.getId();//菜品id


        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);     //设置删除条件

        dishFlavorService.remove(queryWrapper);//删除


        //再重新添加菜品口味

        List<DishFlavor> flavors = dishDto.getFlavors();//菜品口味


        //用流给菜品口味添加菜品id
        flavors.stream().map((item -> {
            item.setDishId(dishId);
            return item;
        })).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(dishDto.getFlavors());


    }

    /**
     * 删除菜品及其口味
     * @param ids
     */
    @Override
    @Transactional
    public void deleteWithFlavor(List<Long> ids) {


        //判断菜品状态，若在售，禁止删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids);
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        int count = this.count(dishLambdaQueryWrapper);

        if(count>0){
            //说明在售中
            throw new CustomException("菜品售卖中，禁止删除");
        }

        //删除菜品
       this.removeByIds(ids);

        //删除口味
        LambdaQueryWrapper<DishFlavor> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(QueryWrapper);


    }

    /**
     * 根据菜品id设置状态
     * @param status
     * @param ids
     */
    @Override
    public void updataStatus(int status,List<Long> ids) {
//        Dish dish = dishMapper.selectById(ids);
//        dish.setStatus(status);
//        dishMapper.updateById(dish);

        for(Long id:ids){
            Dish byId = this.getById(id);
            byId.setStatus(status);
            this.updateById(byId);
        }
    }


    /**
     * 查菜品信息和口味信息,用于修改时的回显
     *
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = dishMapper.selectById(id);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //添加口味信息
        dishDto.setFlavors(flavors);

        return dishDto;

    }


}
