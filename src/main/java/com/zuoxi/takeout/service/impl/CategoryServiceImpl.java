package com.zuoxi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuoxi.takeout.common.CustomException;
import com.zuoxi.takeout.entity.Category;
import com.zuoxi.takeout.entity.Dish;
import com.zuoxi.takeout.entity.Setmeal;
import com.zuoxi.takeout.mapper.CategoryMapper;
import com.zuoxi.takeout.service.CategoryService;
import com.zuoxi.takeout.service.DishService;
import com.zuoxi.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        // 1. 检查分类下的菜品是否有关联，有的话就直接抛出一个异常
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishWrapper);
        if (dishCount > 0) {
            // 还有关联就抛出异常
            throw new CustomException("分类下还有关联菜品，不能删除");
        }

        // 2. 检查分类下的套餐是否有关联，有的话就直接抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        long setmealCount = setmealService.count(setmealWrapper);
        if (setmealCount > 0) {
            throw new CustomException("分类下还有关联套餐，不能删除");
        }

        // 3. 没有关联就可以直接删除该分类
        this.removeById(id);
    }
}
