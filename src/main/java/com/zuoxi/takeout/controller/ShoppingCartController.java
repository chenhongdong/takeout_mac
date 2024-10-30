package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zuoxi.takeout.common.BaseContext;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.entity.ShoppingCart;
import com.zuoxi.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        // 设置加购商品的用户id
        Long currentUid = BaseContext.getCurrentUid();
        shoppingCart.setUserId(currentUid);

        // 查询加购的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);

        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            cartServiceOne.setUpdateTime(LocalDateTime.now());
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUpdateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }


    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);

        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(wrapper);
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);

            Long id = cartServiceOne.getId();
            // 如果数量减到0了，就把对应加购的商品从库里删掉
            if (cartServiceOne.getNumber().equals(0)) {
                // SQL: DELETE FROM shopping_cart WHERE id = ?;
                shoppingCartService.removeById(id);
            } else {
                // SQL: UPDATE FROM shopping_cart SET key=value WHERE id = ?;
                shoppingCartService.updateById(cartServiceOne);
            }
        }

        return R.success(cartServiceOne);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentUid = BaseContext.getCurrentUid();
        // SQL: SELECT * FROM shopping_cart WHERE user_id = ? ORDER BY create_time ASC;
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);
        wrapper.orderByDesc(ShoppingCart::getUpdateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }


    @DeleteMapping("/clean")
    public R<String> clean() {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车成功");
    }
}
