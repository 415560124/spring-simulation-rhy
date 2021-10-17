package com.spring;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:31
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
public class BeanDefinition {
    public BeanDefinition(String name, String scope, Class clazz) {
        this.name = name;
        this.scope = scope;
        this.clazz = clazz;
    }

    public String name;
    public String scope;
    public Class clazz;
}
