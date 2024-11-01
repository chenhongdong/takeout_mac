package com.zuoxi.takeout.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders implements Serializable {
    private static final long serialVersionUid = 1L;
    private Long id;
    private String number;  // 订单号
    private Integer status;  // 订单状态 1代付款，2待派送，3已派送，4已完成，5已取消
    private Long userId;
    private Long addressBookId;
    private LocalDateTime orderTime;    // 下单时间
    private LocalDateTime checkoutTime;  // 支付时间
    private Integer payMethod;  // 支付方式 1微信，2支付宝
    private BigDecimal amount;  // 总金额
    private String remark;  // 备注信息
    private String phone;
    private String address;
    private String userName;
    private String consignee;   // 收货人
}
