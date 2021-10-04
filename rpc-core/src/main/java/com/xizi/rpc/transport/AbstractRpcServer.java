package com.xizi.rpc.transport;

import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.annotation.ServiceScan;
import com.xizi.rpc.enumeration.RpcError;
import com.xizi.rpc.exception.RpcException;
import com.xizi.rpc.provider.ServiceProvider;
import com.xizi.rpc.registry.ServiceRegistry;
import com.xizi.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.Set;

// 抽象类AbstractRpcServer 实现RpcServer接口
public abstract class AbstractRpcServer implements RpcServer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // 主机号和端口号
    protected String host;
    protected int port;

    // 在创建 RpcServer 对象时，传入一个 ServiceRegistry 作为这个服务的注册表。
    protected ServiceRegistry serviceRegistry;
    // 在创建 RpcServer 对象时，传入一个 serviceProvider 本地服务提供者
    protected ServiceProvider serviceProvider;

    //扫描服务 将scanServices 方法放在抽象类中，而 start 方法则由具体实现类来实现。
    public void scanServices() {
        // 我们首先需要获得要扫描的包的范围，就需要获取到 ServiceScan 注解的值，而我们前面说过，这个注解是加在启动类上的
        // 那么，我们怎么知道启动类是哪一个呢？答案是通过调用栈。
        // 方法的调用和返回是通过方法调用栈来实现的，当调用一个方法时，该方法入栈，该方法返回时，该方法出站，控制回到栈顶的方法。
        // 那么，main 方法一定位于调用栈的最底端，在 ReflectUtils 中，我写了一个 getStackTrace 方法，用于获取 main 所在的类。
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            //通过 Class 对象的 isAnnotationPresent 方法来判断该类是否有 @ServiceScan 注解。
            if(!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        // 如果有，通过startClass.getAnnotation(ServiceScan.class).value(); 获取注解的值。
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        //启动类上 value值为"" 没有进行设置
        if("".equals(basePackage)) {
            //获取到 com.xizi.test包名
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // 通过ReflectUtil.getClasses(basePackage)包名下的获取到所有的 Class 了
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            // 逐个判断是否有 @Service 注解，如果有的话，通过反射创建该对象，并且调用 publishService 注册即可。
            if(clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    //创建该对象的实例
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                //若服务名称为"" 默认使用全限定类名为服务名称
                if("".equals(serviceName)) {
                    //获取该类实现的接口集合
                    Class<?>[] interfaces = clazz.getInterfaces();
                    //遍历集合 将服务进行注册
                    for (Class<?> oneInterface: interfaces){
                        // publishService 需要将服务保存在本地的注册表，同时注册到 Nacos 上
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    // publishService 需要将服务保存在本地的注册表，同时注册到 Nacos 上。
                    publishService(obj, serviceName);
                }
            }
        }
    }

    // publishService 需要将服务保存在本地的注册表，同时注册到 Nacos 上。
    @Override
    public <T> void publishService(T service, String serviceName) {
        //添加服务到本地注册表
        serviceProvider.addServiceProvider(service, serviceName);
        //注册服务到Nacos注册中心上去(根据服务名称,IP+端口号)
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
