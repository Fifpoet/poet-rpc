package org.fifpoet.rpc.strategy.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.entity.RpcRequest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * the load balance strategy of registry center
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances, RpcRequest request);
    default List<Instance> filterUnhealthyNode(List<Instance> instances){
        return instances.stream().filter(Instance::isHealthy).filter(Instance::isEnabled).collect(Collectors.toList());
    }
    static LoadBalancer getDefaultBalancer() {
        return new RandomLoadBalancer();
    }
}
