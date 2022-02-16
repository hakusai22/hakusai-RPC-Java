package com.xizi.test;

import com.xizi.rpc.annotation.Service;
import com.xizi.rpc.api.RpcRequest1;

/**
 * @Author yinpeng8
 * @Date 2022/2/16 14:02
 * @Description:
 */
@Service
public class RpcRequestImpl1 implements RpcRequest1 {
    @Override
    public String rpcRequest(String s) {
        return "rpcRequest1";
    }
}
