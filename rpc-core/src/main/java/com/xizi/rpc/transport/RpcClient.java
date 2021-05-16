package com.xizi.rpc.transport;


import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 *
 * @author xizizz
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
