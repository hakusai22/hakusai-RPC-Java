package com.xizi.test;


import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.api.ByeService;

// ByeService的实现类
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye 拜拜, " + name;
    }
}
