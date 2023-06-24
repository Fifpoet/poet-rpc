package org.fifpoet.rpc.registry;

import java.net.InetSocketAddress;

/**
 * register center common interface
 */
public interface ServiceRegistry {

    void register(String serviceName, InetSocketAddress inetSocketAddress);
    InetSocketAddress lookupService(String serviceName);
}
