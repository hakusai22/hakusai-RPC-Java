package com.xizi.test;

import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.api.HelloObject;
import com.xizi.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// HelloService服务的实现类  服务端的实现类
// 在 HelloServiceImpl 类上加上 @service 注解 将服务自动注册到Nacos服务注册中心上去
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到消息：{}", object.getMessage());
        return "这是HelloServiceImpl方法";
    }

}
