package com.hakusai.rpc.transport;


import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口 发送请求和默认序列化机制kryo
 *
 * @author hakusai22@qq.com
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    // 发送请求 调用服务
    Object sendRequest(RpcRequest rpcRequest);

}
