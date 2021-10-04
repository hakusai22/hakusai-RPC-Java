package com.xizi.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示一个服务提供类，用于远程接口的实现类
 * 基于注解进行服务的自动注册。
 * @Service 放在一个类上，标识这个类提供一个服务
 * @author xizizzz
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

     //Service 注解的值定义为该服务的名称，默认值是该类的完整类名
     String name() default "";

}
