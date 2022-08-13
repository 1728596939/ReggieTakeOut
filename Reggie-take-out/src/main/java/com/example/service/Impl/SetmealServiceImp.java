package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.CustomException;
import com.example.dto.DishDto;
import com.example.dto.SetmealDto;
import com.example.entity.*;
import com.example.mapper.SetmealMapper;
import com.example.service.CategoryService;
import com.example.service.SetmealDishService;
import com.example.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class SetmealServiceImp extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {


    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 分页查询套餐信息和 分类名称
     * @param currentPage
     * @param pageSize
     * @return
     */
    public IPage getPage(int currentPage,int pageSize,String name){

        //  条件构造器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //分页构造器
        IPage<Setmeal> pageInfo = new Page(currentPage, pageSize);
        IPage<SetmealDto> setmealDtoIpage = new Page();

        //添加排序条件
        lqw.orderByDesc(Setmeal::getUpdateTime);

        //添加过滤条件
        lqw.like(name != null, Setmeal::getName, name);

        //执行分页查询
        pageInfo =setmealMapper.selectPage(pageInfo, lqw);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoIpage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {

            //创建对象
            SetmealDto setmealDto = new SetmealDto();
            //拷贝其他属性
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();//分类Id
            Category category = categoryService.getById(categoryId);//查到的分类类
            String categoryName = category.getName();//分类名

            //设置分类名属性
          setmealDto.setCategoryName(categoryName);

            return setmealDto;

        }).collect(Collectors.toList());

        setmealDtoIpage.setRecords(list);


        log.info("执行套餐分页查询");
        return setmealDtoIpage;

    }



    /**
     * 保存套餐信息，及其菜品
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        //先保存套餐信息
        setmealMapper.insert(setmealDto);//setmealDto继承了setmeal套餐，直接封装

        //再保存套餐菜品信息
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        //设置这些菜品的套餐id
        setmealDishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return  item;
        }).collect(Collectors.toList());

        //最后将菜品信息保存到中间表
        setmealDishService.saveBatch(setmealDishList);

    }


    /**
     * 根据id查询套餐分类，用于修改时的回显
     * @param id
     * @return
     */
    public SetmealDto setmealWithDish(Long id) {
        //查询套餐基本信息
       Setmeal setmeal = setmealMapper.selectById(id);

        //查询套餐里的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishlist = setmealDishService.list(queryWrapper);

        //拷贝套餐信息
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //添加菜品信息
        setmealDto.setSetmealDishes(dishlist);

        return setmealDto;

    }


    /**
     * 更新套餐信息，及其包含菜品
     * @param setmealDto
     */
    @Override
    public void upDataSetmealWithDish(SetmealDto setmealDto) {

        //修改套餐的基本信息到菜品Setmeal表
        setmealMapper.updateById(setmealDto);//因为SetmealDto继承了Setmeal，所以直接封装

        //清理当前菜品信息，再重新添加
        Long setmealId = setmealDto.getId();//套餐id，用于中间表单的删除条件


        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);     //设置删除条件

       setmealDishService.remove(queryWrapper);//删除菜品


        //再重新添加菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();//菜品信息集合


        //用流给菜品口味添加菜品id
       setmealDishes.stream().map((item -> {
            item.setSetmealId(setmealId);
            return item;
        })).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());


    }


    /**
     * 更新套餐状态
     * @param status
     * @param ids
     */
    @Override
    public void setmealUpdataStatus(int status, List<Long> ids) {

        for(Long id:ids){
            Setmeal byId = this.getById(id);
            byId.setStatus(status);
            this.updateById(byId);
        }


    }


    /**
     * 删除套餐
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithSetmeal(List<Long> ids) {

        //查询状态，若启用状态，不能删除
        // select  count(*) from Setmeal where id in  (ids) and status=1
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1); //状态等于1 ，即启用
        int count = this.count(setmealLambdaQueryWrapper);

        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        else{
            //删除套餐--Setmeal
            this.removeByIds(ids);  //this 是 IService

            //删除中间表中的菜品 ---SetmealDish
            LambdaQueryWrapper<SetmealDish> QueryWrapper = new LambdaQueryWrapper<>();
            QueryWrapper.in(SetmealDish::getSetmealId,ids);
            setmealDishService.remove(QueryWrapper);

        }




    }


    /**
     * 根据种类查找套餐和其中菜品
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    public List<SetmealDto> getSetmealByCategory(Long id, int status) {

        //查找套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,status);
        List<Setmeal> setmealList = setmealMapper.selectList(setmealLambdaQueryWrapper);

        //查找套餐内的菜品

          List<SetmealDto> setmealDtosList=  setmealList.stream().map((item)->{

            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,item.getId());
            List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

            SetmealDto setmealDto = new SetmealDto();

            //拷贝
            BeanUtils.copyProperties(item,setmealDto);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;

        }).collect(Collectors.toList());




                  return setmealDtosList;
    }
}
