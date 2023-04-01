package com.hakusai.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hakusai.rpc.enumeration.RpcError;
import com.hakusai.rpc.exception.RpcException;
import com.hakusai.rpc.loadbanlancer.LoadBalancer;
import com.hakusai.rpc.loadbanlancer.RandomLoadBalancer;
import com.hakusai.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 客户端  Nacos的服务发现
 *
 * @author hakusai22@qq.com
 */
public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);

    //负载均衡算法 默认随机
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) this.loadBalancer = new RandomLoadBalancer();
        else this.loadBalancer = loadBalancer;
    }

    //查询服务 使用NIO的InetSocketAddress建立客户端和服务端网络通信
    // lookupService 方法则是根据服务名称从注册中心获取到一个服务提供者的地址。
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            //根据服务名获取Nacos中全部的服务实例
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            //通过 getAllInstance 获取到某个服务的所有提供者列表后，需要选择一个
            //采用负载均衡策略 进行服务的处理
            Instance instance = loadBalancer.select(instances);
            // 根据服务的IP地址和端口号创建套接字地址。
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
        }
        return null;
    }

}
