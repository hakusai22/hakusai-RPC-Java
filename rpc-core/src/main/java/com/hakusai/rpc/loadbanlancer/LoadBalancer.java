package com.hakusai.rpc.loadbanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 负载均衡进行全部的方法处理
 *
 * @author hakusai22@qq.com
 */
public interface LoadBalancer {

    Instance select(List<Instance> instances);

}
