package com.spring.annotation;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:39
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Herion Lemon
 * @date: 2021/10/17 20:03
 * @slogan: 如果你想攀登高峰，切莫把彩虹当梯子
 * @description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    String value() default "";
}
