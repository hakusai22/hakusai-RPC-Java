package com.hakusai.test;


import com.hakusai.rpc.api.ByeService;
import com.hakusai.rpc.api.HelloObject;
import com.hakusai.rpc.api.HelloService;
import com.hakusai.rpc.serializer.CommonSerializer;
import com.hakusai.rpc.transport.RpcClientProxy;
import com.hakusai.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试用消费者（客户端）
 *
 * @author hakusai22@qq.com
 */
public class SocketTestClient {

    private static final Logger LOG = LoggerFactory.getLogger(SocketTestClient.class);

    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "Client发送信息: remake");
        String res = helloService.hello(object);
        LOG.info(res);
        ByeService byeService = proxy.getProxy(ByeService.class);
        LOG.info(byeService.bye("remake"));
    }

}
