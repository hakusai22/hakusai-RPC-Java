package com.xizi.test;


import com.xizi.rpc.annotation.ServiceScan;
import com.xizi.rpc.serializer.CommonSerializer;
import com.xizi.rpc.transport.RpcServer;
import com.xizi.rpc.transport.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 *
 * @author xizizzz
 */
@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }

}
