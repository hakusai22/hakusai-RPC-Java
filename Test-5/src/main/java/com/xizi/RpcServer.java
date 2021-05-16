package com.xizi;

public interface RpcServer {
    void start(int port);

    <T> void publishService(Object service, Class<T> serviceClass);

}