package com.xizi.rpc.transport;


import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口 发送请求和默认序列化机制kryo
 * @author xizizz
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    // 发送请求 调用服务
    Object sendRequest(RpcRequest rpcRequest);

}
