package com.xizi.test;


import com.xizi.rpc.annotation.ServiceScan;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.RpcServer;
import com.xizi.rpc.transport.netty.server.NettyServer;

/**
 * 测试用Netty服务提供者（服务端）
 * @ServiceScan 自定义服务扫描注解
 * @author xizizzz
 */
@ServiceScan(value = "com.xizi.test") // 并且在服务器启动类上加上注解
public class NettyTestServer {

    public static void main(String[] args) {
        //1. 创建NettyServer对象 和序列化机制protobuf+主机号+端口
        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.KRYO_SERIALIZER);
        //2. 开启服务端
        server.start();
    }

}
