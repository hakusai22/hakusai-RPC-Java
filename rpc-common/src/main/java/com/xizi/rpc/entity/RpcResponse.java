package com.xizi.rpc.entity;

import com.xizi.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


//提供者执行完成或出错后向消费者返回的结果对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> implements Serializable {

    // 响应对应的请求号
    private String requestId;
    // 响应状态码
    private Integer statusCode;
    // 响应状态补充信息
    private String message;
    // 响应数据
    private T data;


    // 两个静态方法，用于快速生成成功与失败的响应对象。
    // 返回成功
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setData(data);
        return response;
    }
    // 返回失败
    public static <T> RpcResponse<T> fail(ResponseCode code, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());
        response.setMessage(code.getMessage());
        return response;
    }
}
