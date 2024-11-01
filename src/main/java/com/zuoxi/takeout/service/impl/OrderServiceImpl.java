package com.zuoxi.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zuoxi.takeout.common.BaseContext;
import com.zuoxi.takeout.entity.*;
import com.zuoxi.takeout.mapper.OrderMapper;
import com.zuoxi.takeout.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        // 查询当前用户id
        Long currentUid = BaseContext.getCurrentUid();

        User user = userService.getById(currentUid);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        // 查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentUid);

        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        // 创建订单id
        long orderId = IdWorker.getId();
        // 实付总金额，初始值0
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetailList = list.stream().map(item -> {
            OrderDetail detail = new OrderDetail();
            detail.setName(item.getName());
            detail.setImage(item.getImage());
            detail.setOrderId(orderId);
            detail.setDishId(item.getDishId());
            detail.setSetmealId(item.getSetmealId());
            detail.setDishFlavor(item.getDishFlavor());
            detail.setNumber(item.getNumber());
            detail.setAmount(item.getAmount());
            // 累加amount金额，菜品单价 * 数量
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return detail;
        }).collect(Collectors.toList());


        // 提交的参数只有addressBookId、payMethods、remark，所以剩下缺的字段需要都设置上
//        fillOrder(orders, currentUid, addressBook, orderId, amount);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        String address = (addressBook.getProvinceName() != null ? addressBook.getProvinceName() : "") + (addressBook.getCityName() != null ? addressBook.getCityName() : "") + (addressBook.getDistrictName() != null ? addressBook.getDistrictName() : "") + (addressBook.getDetail() != null ? addressBook.getDetail() : "");
        orders.setAddress(address);
        // 存入订单表一条数据
        this.save(orders);

        // 存入订单详细表多条数据
        orderDetailService.saveBatch(orderDetailList);

        // 清空购物车
        shoppingCartService.remove(wrapper);
    }

    @Override
    @Transactional
    public void submitOrder(Orders orders) {
        // 根据用户id获取购物车数据
        Long uid = BaseContext.getCurrentUid();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, uid);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);

        User user = userService.getById(uid);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());

        long orderId = IdWorker.getId();
        // 总金额
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            String[] ignoreProps = {"user_id", "create_time"};
            BeanUtils.copyProperties(item, orderDetail, ignoreProps);
            orderDetail.setOrderId(orderId);
            // 累加计算总价
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).toList();

        // 添加一条数据到订单表orders
        // 填充订单其余字段
        fillOrder(orders, uid, user, addressBook, orderId, amount);
        this.save(orders);

        // 添加多条数据到订单详情表order_detail
        orderDetailService.saveBatch(orderDetails);
        // 清空购物车
        shoppingCartService.remove(wrapper);
    }

    private void fillOrder(Orders orders, Long uid, User user, AddressBook addressBook, long orderId, AtomicInteger amount) {
        orders.setId(orderId);
        orders.setNumber(orderId + "");
        orders.setStatus(2);
        orders.setUserId(uid);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone());
        String address = (addressBook.getProvinceName() != null ? addressBook.getProvinceName() : "")
                + (addressBook.getCityName() != null ? addressBook.getCityName() : "")
                + (addressBook.getDistrictName() != null ? addressBook.getDistrictName() : "")
                + (addressBook.getDetail() != null ? addressBook.getDetail() : "");
        orders.setAddress(address);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
    }
}
