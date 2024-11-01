package com.zuoxi.takeout.dto;

import com.zuoxi.takeout.entity.OrderDetail;
import com.zuoxi.takeout.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;
}
