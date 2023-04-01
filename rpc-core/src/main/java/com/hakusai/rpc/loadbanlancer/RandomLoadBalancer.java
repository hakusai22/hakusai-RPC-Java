package com.hakusai.rpc.loadbanlancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * 随机算法 负载均衡
 *
 * @author hakusai22@qq.com
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public Instance select(List<Instance> instances) {
        //集合中随机返回一个服务对象
        return instances.get(new Random().nextInt(instances.size()));
    }

}
