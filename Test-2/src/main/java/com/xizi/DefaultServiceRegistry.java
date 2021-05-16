package com.xizi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


// 这种处理方式也就说明了某个接口只能有一个对象提供服务。
// 默认的注册表类 DefaultServiceRegistry 来实现这个接口ServiceRegistry
public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    //将服务名与提供服务的对象的对应关系保存在一个 ConcurrentHashMap 中
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    //使用一个 Set 来保存当前有哪些对象已经被注册
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void register(T service) {
        //在注册服务时，默认采用这个对象实现的接口的完整类名作为服务名
        String serviceName = service.getClass().getCanonicalName();
        //判断Set集合中是否存在该服务名称
     if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        //获取服务实现的全部接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        //将实现的接口存储到map中去
        for(Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口: {} 注册服务: {}", interfaces, serviceName);
    }

    //获得服务的对象就更简单了，直接去 Map 里查找就行了。
    @Override
    public synchronized Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
