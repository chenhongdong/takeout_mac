package com.zuoxi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuoxi.takeout.entity.Dish;
import com.zuoxi.takeout.mapper.DishMapper;
import com.zuoxi.takeout.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
