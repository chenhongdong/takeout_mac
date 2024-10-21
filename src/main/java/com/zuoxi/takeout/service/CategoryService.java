package com.zuoxi.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zuoxi.takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
