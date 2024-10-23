package com.zuoxi.takeout.dto;

import com.zuoxi.takeout.entity.Dish;
import com.zuoxi.takeout.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    // 口味列表
    private List<DishFlavor> flavors = new ArrayList<>();
    // 分类名称
    private String categoryName;
    // 份数
    private Integer copies;
}
