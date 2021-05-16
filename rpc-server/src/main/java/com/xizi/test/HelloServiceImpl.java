package com.xizi.test;

import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.api.HelloObject;
import com.xizi.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//HelloService服务的实现类
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "这是HelloServiceImpl方法";
    }

}
