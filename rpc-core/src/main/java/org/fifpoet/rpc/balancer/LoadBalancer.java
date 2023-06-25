package org.fifpoet.rpc.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * the load balance strategy of registry center
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
