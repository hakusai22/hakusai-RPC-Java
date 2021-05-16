package com.xizi.rpc.transport;


import com.xizi.rpc.serializer.CommonSerializer;

/**
 * 服务器类通用接口
 *
 * @author xizizzz
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    <T> void publishService(T service, String serviceName);

}
