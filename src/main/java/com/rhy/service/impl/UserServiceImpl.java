package com.rhy.service.impl;

import com.rhy.service.OrderService;
import com.rhy.service.UserService;
import com.spring.annotation.Autowired;
import com.spring.annotation.Component;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:02
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private OrderService orderService;
    @Override
    public void test() {
        System.out.println("test");
    }
}
