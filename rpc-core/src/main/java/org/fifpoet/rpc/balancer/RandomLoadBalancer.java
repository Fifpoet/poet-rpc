package org.fifpoet.rpc.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.entity.RpcRequest;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances, RpcRequest request) {
        return filterUnhealthyNode(instances).get(new Random().nextInt(instances.size()));
    }

}
