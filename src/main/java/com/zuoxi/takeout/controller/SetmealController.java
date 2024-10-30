package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.dto.SetmealDto;
import com.zuoxi.takeout.entity.Category;
import com.zuoxi.takeout.entity.Setmeal;
import com.zuoxi.takeout.entity.SetmealDish;
import com.zuoxi.takeout.service.CategoryService;
import com.zuoxi.takeout.service.SetmealDishService;
import com.zuoxi.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
//        setmealService.saveWithDish(setmealDto);
        setmealService.addWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
   /* @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        // 根据name进行模糊查询
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);


        setmealService.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据分类id查找到分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }*/

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, wrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            // 查找套餐分类
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }


    /**
     * 删除及批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {  // 多个查询参数时ids=11,22,33，@RequestParam是必写的
//        setmealService.removeWithDish(ids);
        setmealService.deleteWithDish(ids);
        return R.success("套餐删除成功");
    }


    /**
     * 根据id查看数据
     * @param id
     * @return
     */
//    @GetMapping("/{id}")
//    public R<SetmealDto> get(@PathVariable Long id) {
//        SetmealDto setmealDto = setmealService.getWithDish(id);
//        return R.success(setmealDto);
//    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
//    @PutMapping
//    public R<String> update(@RequestBody SetmealDto setmealDto) {
//        setmealService.updateWithDish(setmealDto);
//        return R.success("套餐更新成功");
//    }
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish2(setmealDto);
        return R.success("修改套餐成功");
    }


    /**
     * 套餐列表
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        Long categoryId = setmeal.getCategoryId();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId, categoryId);

        List<Setmeal> list = setmealService.list(wrapper);

        return R.success(list);
    }
}
