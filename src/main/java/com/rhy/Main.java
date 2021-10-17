package com.rhy;

import com.rhy.config.AppConfig;
import com.rhy.service.UserService;
import com.spring.RhyAnnotationApplicationContext;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:19
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        RhyAnnotationApplicationContext rhyAnnotationApplicationContext = new RhyAnnotationApplicationContext(AppConfig.class);
        UserService userService = (UserService) rhyAnnotationApplicationContext.getBean("userService");
        userService.test();
    }
}
