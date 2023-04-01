package com.hakusai.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.hakusai.rpc.enumeration.RpcError;
import com.hakusai.rpc.exception.RpcException;
import com.hakusai.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;

/**
 * Nacos服务注册中心
 * 在创建 RpcServer 对象时，传入一个 ServiceRegistry 作为这个服务的注册表。
 *
 * @author hakusai22@qq.com
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    //register 方法将服务的名称和地址注册进服务注册中心
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            //注册服务到Nacos注册中心上去(根据服务名称,IP+端口号)
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
