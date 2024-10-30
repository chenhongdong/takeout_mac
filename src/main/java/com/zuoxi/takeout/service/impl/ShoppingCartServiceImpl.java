package com.zuoxi.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuoxi.takeout.entity.ShoppingCart;
import com.zuoxi.takeout.mapper.ShoppingCartMapper;
import com.zuoxi.takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
