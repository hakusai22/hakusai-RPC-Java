package com.xizi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder  //构建者模式
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    // 待调用接口名称
    private String interfaceName;
    // 待调用方法名称
    private String methodName;
    // 调用方法的参数
    private Object[] parameters;
    // 调用方法的参数类型
    private Class<?>[] paramTypes;

    //服务端知道以上四个条件，就可以找到这个方法并且调用了

}
