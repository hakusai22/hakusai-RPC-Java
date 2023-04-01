package com.hakusai.test;


import com.hakusai.rpc.api.ByeService;
import com.hakusai.rpc.api.HelloObject;
import com.hakusai.rpc.api.HelloService;
import com.hakusai.rpc.api.RpcRequest1;
import com.hakusai.rpc.serializer.CommonSerializer;
import com.hakusai.rpc.transport.RpcClient;
import com.hakusai.rpc.transport.RpcClientProxy;
import com.hakusai.rpc.transport.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试用Netty消费者
 *
 * @author hakusai22@qq.com
 */
public class NettyTestClient {

    private static final Logger LOG = LoggerFactory.getLogger(NettyTestClient.class);

    public static void main(String[] args) {
        //1. 创建Netty的客户端 kryo
        RpcClient client = new NettyClient(CommonSerializer.KRYO_SERIALIZER);
        // 2.创建Rpc客户端代理
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        //3. 根据接口获取代理对象HelloService接口
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        //4. 接口调用hello方法 执行invoke()方法发送请求,实现方法是在服务端
        LOG.info(helloService.hello(new HelloObject(12, "客户端发送信息: remake")));
        //5. 通过Rpc代理服务获取到ByeService服务接口
        ByeService byeService = rpcClientProxy.getProxy(ByeService.class);
        // 6. 调用bye的方法
        LOG.info(byeService.bye("remake "));

        LOG.info(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        LOG.info(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        LOG.info(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        LOG.info(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));
        LOG.info(rpcClientProxy.getProxy(RpcRequest1.class).rpcRequest("测试----------"));

    }

}
