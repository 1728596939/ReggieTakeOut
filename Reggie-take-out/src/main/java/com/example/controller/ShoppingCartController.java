package com.example.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.BaseContext;
import com.example.common.R;
import com.example.entity.ShoppingCart;
import com.example.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加订单到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart) {


        //设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());

        //查询当前菜品或套餐，若存在，只需修改数量
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //当前用户的购物车
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //根据菜品ID
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());

        //根据套餐Id
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        //查询
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if (cartServiceOne != null) {
            //存在，数量加1
            Integer number = cartServiceOne.getNumber();
            number++;
            cartServiceOne.setNumber(number);

            boolean b = shoppingCartService.updateById(cartServiceOne);

        } else {
            //如果不存在。存储。数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);


            cartServiceOne = shoppingCart;

        }
        return R.success(cartServiceOne);

    }

    /**
     * 购物车回显
     *
     * @return
     */
    @GetMapping("/list")
    public R getlist() {

        //根据用户Id查
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);

    }

    /**
     * 减少订单选择
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R sub(@RequestBody ShoppingCart shoppingCart) {
        //先查询，若数量大于1 ，就减1，若等于1 ，删除

        //根据当前用户id
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //根据菜品ID
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());

        //根据套餐Id
        shoppingCartLambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        //查询当前菜品或套餐
        ShoppingCart cartServiceOne = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        //获取选择的数量
        Integer number = cartServiceOne.getNumber();

        //若大于等于2，减1，若等于1.删除
        if (number > 1) {
            number = number - 1;
            cartServiceOne.setNumber(number);

            //更新
            shoppingCartService.updateById(cartServiceOne);
        } else {

            cartServiceOne.setNumber(0);
            shoppingCartService.removeById(cartServiceOne);
        }

        return R.success(cartServiceOne);

    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R delete() {

        //用户Id
        Long currentId = BaseContext.getCurrentId();

        //根据用户ID清空购物车
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        return R.success("清空购物车成功");

    }


}
