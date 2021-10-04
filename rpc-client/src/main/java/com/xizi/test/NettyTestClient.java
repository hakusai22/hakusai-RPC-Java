package com.xizi.test;


import com.xizi.rpc.api.ByeService;
import com.xizi.rpc.api.HelloObject;
import com.xizi.rpc.api.HelloService;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.RpcClient;
import com.xizi.rpc.transport.RpcClientProxy;
import com.xizi.rpc.transport.netty.client.NettyClient;

/**
 * 测试用Netty消费者
 *
 * @author xizizzz
 */
public class NettyTestClient {

    public static void main(String[] args) {
        //1. 创建Netty的客户端 kryo
        RpcClient client = new NettyClient(CommonSerializer.KRYO_SERIALIZER);
        // 2.创建Rpc客户端代理
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        //3.根据接口获取代理对象HelloService接口
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "戏子666666");
        //4. 接口调用hello方法 执行invoke()方法发送请求,实现方法是在服务端
        String res = helloService.hello(object);
        System.out.println(res);
        //5.通过Rpc代理服务获取到ByeService服务接口
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        // 6. 调用bye的方法
        System.out.println(byeService.bye("戏子zzz"));
    }

}
