package com.hakusai.test;

import com.hakusai.rpc.annotation.Service;
import com.hakusai.rpc.api.HelloObject;
import com.hakusai.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HelloService服务的实现类  服务端的实现类.
 * 在 HelloServiceImpl 类上加上 @service 注解 将服务自动注册到Nacos服务注册中心上去.
 *
 * @author hakusai22@qq.com
 */
@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger Log = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        Log.info("服务端接收到消息：{}", object.getMessage());
        return "这是HelloServiceImpl方法";
    }

}
