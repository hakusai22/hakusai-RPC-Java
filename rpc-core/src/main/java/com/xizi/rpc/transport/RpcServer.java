package com.xizi.rpc.transport;


import com.xizi.rpc.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 * @author xizizzz
 */
public interface RpcServer {

    //服务端默认序列化协议
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    //开启
    void start();

    //用于向 Nacos 注册服务
    <T> void publishService(T service, String serviceName);

}
