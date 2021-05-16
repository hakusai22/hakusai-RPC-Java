package com.xizi;

public interface ServiceProvider {
    //注册服务
    <T> void register(T service);
    //获取服务对象
    Object getService(String serviceName);
}
