package com.xizi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//需要实现Serializable接口，因为它需要在调用过程中从客户端传递给服务端。
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到：{}", object.getMessage());
        return "这是掉用的返回值，id=" + object.getId();
    }
}
