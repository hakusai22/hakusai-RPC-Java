package com.xizi.rpc.provider;

/**
 * 保存和提供服务实例对象
 * @author xizizzz
 */
public interface ServiceProvider {

    //本地服务表添加服务提供者
    <T> void addServiceProvider(T service, String serviceName);
    //本地服务表获取服务提供者
    Object getServiceProvider(String serviceName);

}
