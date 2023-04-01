package com.hakusai.test;

import com.hakusai.rpc.annotation.Service;
import com.hakusai.rpc.api.RpcRequest1;

/**
 * @Author yinpeng8
 * @Date 2022/2/16 14:02
 * @Description:
 */
@Service
public class RpcRequestImpl implements RpcRequest1 {
    @Override
    public String rpcRequest(String s) {
        return "rpcRequest1";
    }
}
