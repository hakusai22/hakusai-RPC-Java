package com.xizi.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xizizzz
 */
@AllArgsConstructor
@Getter
public enum PackageType {

    //请求和响应包
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;

}