package com.hakusai.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务扫描的基包
 *
 * @author hakusai22@qq.com
 * @ServiceScan 放在启动的入口类上（main 方法所在的类），标识服务的扫描的包的范围。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {

    // 而 ServiceScan 的值定义为扫描范围的根包，默认值为入口类所在的包
    // 扫描时会扫描该包及其子包下所有的类，找到标记有 Service 的类，并注册。
    String value() default "";

}
