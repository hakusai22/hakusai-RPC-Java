package com.hakusai.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hakusai22@qq.com
 */

@Getter
@AllArgsConstructor
public enum PackageType {

    //请求和响应包
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}