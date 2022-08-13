package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.common.R;
import com.example.dto.DishDto;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.DishFlavor;
import com.example.service.Impl.DishFlavorServiceImp;
import com.example.service.Impl.DishServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜名管理
 */

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    //菜品
    @Autowired
    private DishServiceImp dishServiceImp;


    //菜品口味
    @Autowired
    private DishFlavorServiceImp dishFlavorServiceImp;


    /**
     * 分页查询菜品信息
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R getPage(int page, int pageSize, String name) {


        log.info("查询页数为:{},查询条数为:{}", page, pageSize);

        //调用查询方法
        IPage Ipage = dishServiceImp.getPage(page, pageSize, name);
        //如果删除后，查询页数大于可查的最大页数，则查询最大页数
        if (page > Ipage.getPages()) {
            Ipage = dishServiceImp.getPage((int) Ipage.getPages(), pageSize, name);
        }


        return R.success(Ipage);
    }

    /**
     * 添加菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());

        dishServiceImp.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    /**
     * 根据ID查询菜品信息和对应的口味信息，用于修改菜品时的回显示
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByid(@PathVariable Long id) {

        DishDto byId = dishServiceImp.getByIdWithFlavor(id);


        return R.success(byId);
    }


    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R upDateById(@RequestBody DishDto dishDto) {

        dishServiceImp.upDataByIdWithFlavor(dishDto);
        return R.success("修改菜品成功");

    }

    /**
     * 删除菜品信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R deleteById(@RequestParam List<Long> ids) {

        //调用方法，删除
        dishServiceImp.deleteWithFlavor(ids);

        return R.success("删除菜品成功");
    }


    /**
     * 修改菜品状态
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R updataStatus(@PathVariable int status,@RequestParam List<Long> ids) {
        log.info("要修改菜品的状态为：{}，菜品id为：{}", status, ids);
        dishServiceImp.updataStatus(status, ids);

        return R.success("修改菜品状态成功");

    }


    /**
     * 根据种类或名称查找菜品，用于添加套餐时的回显,和前端菜品页面的展示
     *
     * @param dish
     * @return
     */
   /* @GetMapping("/list")
    public R getDishByCategory(Dish dish) {

        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //设置条件


        //当根据id查找时
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        //根据名称模糊查找时
        dishLambdaQueryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());

        //只查状态等于1，在售的菜品
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        //查找集合
        List<Dish> dishList = dishServiceImp.list(dishLambdaQueryWrapper);

        return R.success(dishList);


    }*/







    /**
     * 根据种类或名称查找菜品，用于添加套餐时的回显,和前端菜品页面的展示
     *
     * @param dish
     * @return
     */
       @GetMapping("/list")
    public R getDishByCategory(Dish dish) {

        //条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //设置条件

        //当根据id查找时
        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

        //根据名称模糊查找时
        dishLambdaQueryWrapper.like(dish.getName() != null, Dish::getName, dish.getName());

        //只查状态等于1，在售的菜品
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        //查找菜品集合
        List<Dish> dishList = dishServiceImp.list(dishLambdaQueryWrapper);

        //查找菜品口味
          List<DishDto> dishDtoList= dishList.stream().map((item)->{
               LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
               queryWrapper.eq(DishFlavor::getDishId,item.getId());
              List<DishFlavor>  flavorList= dishFlavorServiceImp.list(queryWrapper);
               DishDto dishDto = new DishDto();
              BeanUtils.copyProperties(item,dishDto);

               dishDto.setFlavors(flavorList);
               return  dishDto;

           }).collect(Collectors.toList());


           return R.success(dishDtoList);


    }


}
