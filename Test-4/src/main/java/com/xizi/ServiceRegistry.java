package com.xizi;

//服务注册表
public interface ServiceRegistry {
    //注册服务
    <T> void register(T service);
    //获取服务对象
    Object getService(String serviceName);
}
