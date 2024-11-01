package com.zuoxi.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zuoxi.takeout.common.BaseContext;
import com.zuoxi.takeout.common.R;
import com.zuoxi.takeout.dto.OrderDto;
import com.zuoxi.takeout.entity.OrderDetail;
import com.zuoxi.takeout.entity.Orders;
import com.zuoxi.takeout.service.OrderDetailService;
import com.zuoxi.takeout.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
//        orderService.submit(orders);
        orderService.submitOrder(orders);
        return R.success("订单提交成功");
    }


    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> dtoPage = new Page<>();

        Long uid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, uid);
        wrapper.orderByDesc(Orders::getOrderTime);
        Page<Orders> orders = orderService.page(pageInfo, wrapper);
        log.info("订单列表:{}", orders.toString());

        List<OrderDto> orderDtoList = orders.getRecords().stream().map(item -> {
            Long orderId = item.getId();
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(item, orderDto);

            LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(OrderDetail::getOrderId, orderId);

            List<OrderDetail> orderDetails = orderDetailService.list(detailWrapper);
            orderDto.setOrderDetails(orderDetails);

            return orderDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(orderDtoList);

        return R.success(dtoPage);
    }
}
