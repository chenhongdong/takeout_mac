package com.zuoxi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuoxi.takeout.common.CustomException;
import com.zuoxi.takeout.dto.SetmealDto;
import com.zuoxi.takeout.entity.Setmeal;
import com.zuoxi.takeout.entity.SetmealDish;
import com.zuoxi.takeout.mapper.SetmealMapper;
import com.zuoxi.takeout.service.SetmealDishService;
import com.zuoxi.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional  // 事务注解：保证数据的一致性，要么全成功，要么全失败
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息
        this.save(setmealDto);
        // 套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(wrapper);
        // 有在售的就不能删除，抛个异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 只有停售状态的套餐才可删除
        // 先删除套餐表中的数据   setmeal表
        this.removeByIds(ids);
        // 删除关系表中的数据    setmeal_dish表
        LambdaQueryWrapper<SetmealDish> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(wrapper2);    // 批量删除
    }

    @Override
    @Transactional
    public SetmealDto getWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 更新套餐基本信息
        this.updateById(setmealDto);
        // 清空菜品数据 setmeal_dish表
        Long setmealId = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(wrapper);

        // 再重新添加菜品数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        long count = this.count(wrapper);
        if (count > 0) {
            throw new CustomException("该套餐还有关联的菜品，不能删除");
        }
        // 只有停售的才能删除
        // 删除套餐表
        this.removeByIds(ids);

        // 删除套餐和菜品关系表
        LambdaQueryWrapper<SetmealDish> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(wrapper2);
    }

    @Override
    @Transactional
    public void addWithDish(SetmealDto setmealDto) {
        // 添加套餐基本信息
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        Long setmealId = setmeal.getId();
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(wrapper);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateWithDish2(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        Long setmealId = setmealDto.getId();

        // 清除套餐菜品关联的数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(wrapper);

        // 再重新添加到setmeal_dish表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        log.info(setmealDishes.toString());
        setmealDishService.saveBatch(setmealDishes);
    }
}
