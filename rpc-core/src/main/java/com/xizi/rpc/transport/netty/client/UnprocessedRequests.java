package com.xizi.rpc.transport.netty.client;


import com.xizi.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的请求map
 * @author xizizzz
 */
public class UnprocessedRequests {

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    //从本地未处理的表删除服务端已经处理的请求
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
