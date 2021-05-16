package com.xizi;

//服务注册表
public interface ServiceRegistry {
    <T> void register(T service);
    Object getService(String serviceName);
}
