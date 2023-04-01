package com.hakusai.rpc.hook;

import com.hakusai.rpc.factory.ThreadPoolFactory;
import com.hakusai.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用钩子 清除Nacos中全部的服务和关闭线程池
 * 钩子是什么呢？是在某些事件发生后自动去调用的方法。
 * 那么我们只需要把注销服务的方法写到关闭系统的钩子方法里就行了。
 *
 * @author hakusai22@qq.com
 */

//关闭后将自动注销所有服务钩子
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    //饿汉式单例模式 安全
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        // 这个方法的意思就是在jvm中增加一个关闭的钩子，
        // 当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
        // 当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //使用Nacos的工具类进行关闭注册服务
            NacosUtil.clearRegistry();
            //线程池 关闭所有线程
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
