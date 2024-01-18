package org.fifpoet.rpc.endpoints.registry;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * register center common interface
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);

    List<Instance> getAllInstance(String serviceName);

    static ServiceRegistry getDefaultRegistry() {
        return new NacosServiceRegistry();
    }
}
