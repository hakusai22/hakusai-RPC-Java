package com.xizi;

public class TestClient {
    public static void main(String[] args) {
        //ip地址+端口号获取接口的代理服务
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);

        //创建对象 传入进去
        HelloObject object = new HelloObject(12, "戏子66666");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
