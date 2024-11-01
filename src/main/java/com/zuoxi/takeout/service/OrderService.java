package com.zuoxi.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zuoxi.takeout.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);

    void submitOrder(Orders orders);
}
