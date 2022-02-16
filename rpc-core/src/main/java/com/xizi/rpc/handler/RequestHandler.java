package com.xizi.rpc.handler;

import com.xizi.rpc.entity.RpcRequest;
import com.xizi.rpc.entity.RpcResponse;
import com.xizi.rpc.enumeration.ResponseCode;
import com.xizi.rpc.provider.ServiceProvider;
import com.xizi.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进行过程调用的处理器
 * @author xizizzz
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    //根据接口名称获取服务 进行调用目标方法
    public Object handle(RpcRequest rpcRequest) {
        //根据接口名称从本地注册表中获取服务
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        //调用服务方法
        return invokeTargetMethod(rpcRequest, service);
    }

    //调用目标方法 使用反射
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            //根据方法名和请求参数获取Method对象
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            //根据服务和请求参数进行调用
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            logger.info("调用方法结果:{}", result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND, rpcRequest.getRequestId());
        }
        return result;
    }

}
