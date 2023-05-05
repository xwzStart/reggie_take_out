package com.it.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.reggie.common.BaseContext;
import com.it.reggie.common.CustomException;
import com.it.reggie.entity.*;
import com.it.reggie.mapper.OrderMapper;
import com.it.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        log.info("--------userId{}",userId);

        //查询当前用户获取购物车数据
        LambdaQueryWrapper<ShoppingCart> ordersWrapper = new LambdaQueryWrapper<>();
        ordersWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> cartList = shoppingCartService.list(ordersWrapper);

        //判断购物车是否为空
        if(cartList == null && cartList.size() ==  0){
            throw new CustomException("购物车不能为空");
        }
        //获取用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if(addressBook == null){
            throw new CustomException("地址不能为空");
        }


        AtomicInteger amount = new AtomicInteger(0);
        
        //获取订单号
        long id = IdWorker.getId();

        //组装订单明细表

        List<OrderDetail> detailList = cartList.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();

            orderDetail.setOrderId(id);
            //拷贝
            BeanUtils.copyProperties(item,orderDetail);

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());



        //组装订单表
        orders.setId(id);
        orders.setNumber(String.valueOf(id));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setPayMethod(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName()) +
                (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName()) +
                (addressBook.getCityName() == null ? "" : addressBook.getCityName()) +
                        (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入一条数据
        this.save(orders);
        //向订单明细表中插入多条数据
        orderDetailService.saveBatch(detailList);
        //清空购物车
        shoppingCartService.remove(ordersWrapper);


    }
}