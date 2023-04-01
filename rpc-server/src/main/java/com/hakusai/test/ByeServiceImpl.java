package com.hakusai.test;


import com.hakusai.rpc.annotation.Service;
import com.hakusai.rpc.api.ByeService;

/**
 * ByeService的实现类  服务端的实现类 将服务自动注册到Nacos注册中心上去.
 *
 * @author hakusai22@qq.com
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye 拜拜, " + name;
    }
}
