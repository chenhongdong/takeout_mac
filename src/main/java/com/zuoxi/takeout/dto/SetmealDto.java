package com.zuoxi.takeout.dto;

import com.zuoxi.takeout.entity.Setmeal;
import com.zuoxi.takeout.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    // 套餐选择的菜品列表
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
