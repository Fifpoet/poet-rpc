package org.fifpoet.rpc.strategy.router;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface CommonRouter {
    /**
     *
     */
    List<Instance> route(List<Instance> instances);
    static CommonRouter getDefaultRouter() {
        return new IPRouter();
    }
}
