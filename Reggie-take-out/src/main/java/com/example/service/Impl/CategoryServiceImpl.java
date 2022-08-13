package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.common.R;
import com.example.entity.Category;
import com.example.entity.Dish;
import com.example.entity.Setmeal;
import com.example.mapper.CategoryMapper;
import com.example.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishServiceImp dishServiceImp;//菜品业务类

    @Autowired
    private SetmealServiceImp setmealServiceImp;//套餐业务类


    //分页查询
    public IPage<Category> getPage(int currentPage, int pageSize) {
        //条件构造器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        //添加条件
        //  qw.like(Strings.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        qw.orderByAsc(Category::getSort);

        //分页构造器
        IPage page = new Page(currentPage, pageSize);

        page = categoryMapper.selectPage(page, qw);
        log.info("分类执行分页查询");
        return page;
    }


    /**
     * 功能描述
     * 根据id，删除分类，删除之前需要进行判断
     *
     * @param id
     * @return
     * @author Anna.
     * @date
     */
    @Override
    public void remove(long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, id);
        int count = dishServiceImp.count(lqw);
        if (count > 0) {
            //说明该分类与菜品有关联，抛异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        //查询当前分类是否关联了彩屏，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealServiceImp.count(setmealLambdaQueryWrapper);

        if (count1 > 0) {

            //说明与套餐有关联，抛异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        //正常删除分类
        categoryMapper.deleteById(id);


    }

    //分类查询
    public R getType(Category category){
        //条件构造器
        LambdaQueryWrapper<Category>  lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //条件查询
        List<Category> categories = categoryMapper.selectList(lambdaQueryWrapper);
        return R.success(categories);

    }

}
