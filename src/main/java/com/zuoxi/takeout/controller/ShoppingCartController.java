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
        // 设置用户id
        Long currentUid = BaseContext.getCurrentUid();
        shoppingCart.setUserId(currentUid);
        // 加购的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        // 查询
        ShoppingCart cartObj = shoppingCartService.getOne(wrapper);
        // 如果有过加购
        if (cartObj != null) {
            Integer number = cartObj.getNumber();
            cartObj.setNumber(number + 1);
            shoppingCartService.updateById(cartObj);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartObj = shoppingCart;
        }

        return R.success(cartObj);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            wrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number - 1);

            if (cart.getNumber() == 0) {
                shoppingCartService.removeById(cart);
            } else {
                shoppingCartService.updateById(cart);
            }
        }
        return R.success(cart);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        Long currentUid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);
        shoppingCartService.remove(wrapper);
        return R.success("清空购物车");
    }
}
