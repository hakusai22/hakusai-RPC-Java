package com.xizi;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
}