package com.xizi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    //将一个对象发过去，并且接受返回的对象
    //1. 直接使用Java的序列化方式，通过Socket传输。
    //2. 创建一个Socket，获取ObjectOutputStream对象，然后把需要发送的对象传进去即可
    //3. 接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            return null;
        }
    }
}
