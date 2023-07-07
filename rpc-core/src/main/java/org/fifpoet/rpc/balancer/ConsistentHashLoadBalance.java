package org.fifpoet.rpc.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class ConsistentHashLoadBalance implements LoadBalancer{
    @Override
    public Instance select(List<Instance> instances) {
        return null;
    }
}
