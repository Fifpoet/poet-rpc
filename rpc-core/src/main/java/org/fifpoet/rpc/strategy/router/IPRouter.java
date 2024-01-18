package org.fifpoet.rpc.strategy.router;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public class IPRouter implements CommonRouter {
    @Override
    public List<Instance> route(List<Instance> instances) {
        return instances;
    }
}
