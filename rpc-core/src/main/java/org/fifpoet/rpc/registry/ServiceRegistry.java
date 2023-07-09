package org.fifpoet.rpc.registry;

import org.fifpoet.entity.RpcRequest;

import java.net.InetSocketAddress;

/**
 * register center common interface
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);
    InetSocketAddress lookupService(String serviceName, RpcRequest request);
    static ServiceRegistry getByCode(int code) {
        switch (code) {
            case 0:
                return new NacosServiceRegistry();
            default:
                return null;
        }
    }
}
