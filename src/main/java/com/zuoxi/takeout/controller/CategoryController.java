package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.entity.Category;
import com.zuoxi.takeout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        // 根据sort进行升序
        wrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.remove(id);
        return R.success("删除分类成功");
    }


    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }
}

























