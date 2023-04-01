package com.hakusai.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 * 在创建 RpcServer 对象时，传入一个 ServiceRegistry 作为这个服务的注册表。
 *
 * @author hakusai22@qq.com
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表
     *
     * @param serviceName       服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);


}
