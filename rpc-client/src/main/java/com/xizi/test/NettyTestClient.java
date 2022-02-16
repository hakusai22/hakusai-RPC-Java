package com.xizi.test;


import com.xizi.rpc.api.ByeService;
import com.xizi.rpc.api.HelloObject;
import com.xizi.rpc.api.HelloService;
import com.xizi.rpc.api.RpcRequest1;
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
        //3. 根据接口获取代理对象HelloService接口
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //4. 接口调用hello方法 执行invoke()方法发送请求,实现方法是在服务端
        System.out.println(helloService.hello(new HelloObject(12, "客户端发送信息: remake")));
        //5. 通过Rpc代理服务获取到ByeService服务接口
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        // 6. 调用bye的方法
        System.out.println(byeService.bye("remake "));

        System.out.println(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        System.out.println(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        System.out.println(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        System.out.println(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        System.out.println(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));

    }

}
