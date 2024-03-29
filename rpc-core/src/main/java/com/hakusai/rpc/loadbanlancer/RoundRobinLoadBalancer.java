package com.hakusai.rpc.loadbanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * 轮询算法 负载均衡
 *
 * @author hakusai22@qq.com
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }

}
