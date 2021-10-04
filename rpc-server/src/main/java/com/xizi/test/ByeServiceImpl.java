package com.xizi.test;


import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.api.ByeService;

// ByeService的实现类  服务端的实现类 将服务自动注册到Nacos注册中心上去
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye 拜拜, " + name;
    }
}
