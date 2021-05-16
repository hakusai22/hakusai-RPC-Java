package com.xizi.rpc.hook;

import com.xizi.rpc.factory.ThreadPoolFactory;
import com.xizi.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xizizzz
 */

//关闭后将自动注销所有服务钩子
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    //饿汉式单例模式
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //使用Nacos的工具类进行关闭注册服务
            NacosUtil.clearRegistry();
            //线程池 关闭所有线程
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
