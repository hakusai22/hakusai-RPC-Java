package com.xizi.test;


import com.xizi.rpc.api.ByeService;
import com.xizi.rpc.api.HelloObject;
import com.xizi.rpc.api.HelloService;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.RpcClientProxy;
import com.xizi.rpc.transport.socket.client.SocketClient;

/**
 * 测试用消费者（客户端）
 *
 * @author xizizzz
 */
public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "Client发送信息: remake");
        String res = helloService.hello(object);
        System.out.println(res);
        ByeService byeService = proxy.getProxy(ByeService.class);
        System.out.println(byeService.bye("remake"));
    }

}
