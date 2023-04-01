package com.hakusai.test;


import com.hakusai.rpc.annotation.ServiceScan;
import com.hakusai.rpc.serializer.CommonSerializer;
import com.hakusai.rpc.transport.RpcServer;
import com.hakusai.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务提供者（服务端）
 *
 * @author hakusai22@qq.com
 * @ServiceScan 自定义服务扫描注解
 */

// 并且在服务器启动类上加上注解
@ServiceScan(value = "com.hakusai.test")
public class NettyTestServer {

    public static void main(String[] args) {
        //1. 创建NettyServer对象 和序列化机制protobuf+主机号+端口
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.KRYO_SERIALIZER);
        //2. 开启服务端
        server.start();
    }

}
