package com.hakusai.test;


import com.hakusai.rpc.annotation.ServiceScan;
import com.hakusai.rpc.serializer.CommonSerializer;
import com.hakusai.rpc.transport.RpcServer;
import com.hakusai.rpc.transport.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 *
 * @author hakusai22@qq.com
 */
@ServiceScan
public class SocketTestServer {

    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        server.start();
    }

}
