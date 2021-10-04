package com.xizi.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端向服务端发送的请求对象
 * 服务端需要这些信息，才能唯一确定服务端需要调用的接口的方法
 * @author xizizzz
 */

@Data
@Builder  //构建者模式
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    // 请求号
    private String requestId;
    // 待调用接口名称
    private String interfaceName;
    //  待调用方法名称
    private String methodName;
    //  调用方法的参数
    private Object[] parameters;
    //  调用方法的参数类型  直接使用Class对象，其实用字符串也是可以的
    private Class<?>[] paramTypes;
    //  是否是心跳包
    private Boolean heartBeat;
}