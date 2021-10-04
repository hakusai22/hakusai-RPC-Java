package com.xizi.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xizi.rpc.enumeration.RpcError;
import com.xizi.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 管理Nacos连接等工具类
 * @author xizizzz
 */
public class NacosUtil {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);
    // nacos的命名服务
    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    //IP地址+端口号
    private static InetSocketAddress address;
    //默认服务注册地址
    private static final String SERVER_ADDR = "127.0.0.1:8848";

    //连接的过程写在了静态代码块中，在类加载时自动连接。
    static {
        namingService = getNacosNamingService();
    }

    //通过 NamingFactory 创建 NamingService 连接 Nacos
    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    //注册服务
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        //可以直接向 Nacos 注册服务
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    //获取全部的实例
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        //可以获得提供某个服务的所有提供者的列表
        return namingService.getAllInstances(serviceName);
    }

    //首先先写向 Nacos 注销所有服务的方法，这部分被放在了 NacosUtils 中作为一个静态方法，
    public static void clearRegistry() {
        if(!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            // 所有的服务名称都被存储在 NacosUtils 类中的 serviceNames 中
            // 在注销时只需要用迭代器迭代所有服务名，调用 deregisterInstance 即可。
            Iterator<String> iterator = serviceNames.iterator();
            while(iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }
}
