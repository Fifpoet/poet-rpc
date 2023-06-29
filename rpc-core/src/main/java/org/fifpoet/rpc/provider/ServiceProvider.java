package org.fifpoet.rpc.provider;

import org.fifpoet.entity.ServiceConfig;

/**
 * reserve and provide the services.
 */
public interface ServiceProvider {
    /**
     * register one service into local registry
     * @param config service config, contain service instance & version & impl id
     * @param <T> service class
     */
    <T> void addServiceProvider(ServiceConfig config);

    /**
     * get service from map
     * @param serviceName name
     * @return service obj
     */
    Object getServiceProvider(String serviceName);

}
