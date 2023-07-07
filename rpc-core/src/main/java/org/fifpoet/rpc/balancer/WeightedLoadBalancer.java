package org.fifpoet.rpc.balancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.util.LogUtil;

import java.util.List;
import java.util.Random;

/**
 * nacos weight based load-balance
 */
public class WeightedLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        //weight range from 1 to 10000
        double total = instances.stream().mapToDouble(Instance::getWeight).sum();
        double randomVal = new Random().nextDouble() * total;
        double weightSum = 0;
        for (Instance i: instances) {
            weightSum += i.getWeight();
            if (weightSum >= randomVal) {
                return i;
            }
        }
        LogUtil.WARN().warn("some error occurred in weighted load-balancer");
        return instances.get(0);
    }
}
