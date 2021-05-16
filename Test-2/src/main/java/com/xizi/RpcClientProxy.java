package com.xizi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//通过jdk动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端。
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    //需要传递host和port来指明服务端的位置
    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //使用getProxy()方法来生成代理对象
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    //InvocationHandler接口需要实现invoke()方法，来指明代理对象的方法被调用时的动作
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //@Builder注解起作用了 创建对象
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();

        RpcClient rpcClient = new RpcClient();
        //需要生成一个RpcRequest对象，客户端发送出去，然后返回从服务端接收到的结果
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }

}
