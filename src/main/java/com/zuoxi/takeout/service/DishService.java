package com.zuoxi.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zuoxi.takeout.dto.DishDto;
import com.zuoxi.takeout.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
