package com.xizi;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        //传入实现类和端口号进行注册
        rpcServer.register(helloService, 9000);
    }
}
