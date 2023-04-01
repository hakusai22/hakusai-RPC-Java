package com.hakusai.rpc.util;

import com.hakusai.rpc.entity.RpcRequest;
import com.hakusai.rpc.entity.RpcResponse;
import com.hakusai.rpc.enumeration.ResponseCode;
import com.hakusai.rpc.enumeration.RpcError;
import com.hakusai.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 检查响应与请求工具类
 *
 * @author hakusai22@qq.com
 */

public class RpcMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";

    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker() {
    }

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        //返回为null 则是调用失败
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

}
