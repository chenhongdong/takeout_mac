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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
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
    @CacheEvict(value = "setmealCache", allEntries = true)
    @ApiOperation(value = "新增套餐接口")
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
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "name", value = "套餐名称")
    })
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
    @CacheEvict(value = "setmealCache", allEntries = true)  // 清除该分类下的所有缓存数据
    @ApiOperation(value = "删除套餐接口")
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
    @ApiOperation(value = "根据id获取菜品接口")
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
    @ApiOperation(value = "套餐更新接口")
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
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    @ApiOperation(value = "套餐条件查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        Long categoryId = setmeal.getCategoryId();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId, categoryId);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(wrapper);

        return R.success(list);
    }
}
