package com.xizi.rpc.provider;

import com.xizi.rpc.enumeration.RpcError;
import com.xizi.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的服务注册表，保存服务端本地服务
 * 我们将服务名与提供服务的对象的对应关系保存在一个 ConcurrentHashMap 中，
 * 并且使用一个 Set 来保存当前有哪些对象已经被注册。在注册服务时，默认采用这个对象实现的接口的完整类名作为服务名，
 * 例如某个对象 A 实现了接口 X 和 Y，那么将 A 注册进去后，会有两个服务名 X 和 Y 对应于 A 对象。这种处理方式也就说明了某个接口只能有一个对象提供服务。
 * 获得服务的对象就更简单了，直接去 Map 里查找就行了。
 * @author xizizzz
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    // 本地服务注册表
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    //服务名称 去重Set
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    //添加服务到本地注册表
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        //若注册表包含该服务 直接跳过
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    //根据名称本地获取服务
    @Override
    public Object getServiceProvider(String serviceName) {
        // 本地缓存服务获取
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
