package org.fifpoet.rpc.provider;

/**
 * reserve and provide the services.
 */
public interface ServiceProvider {
    /**
     * register one service into registry
     * @param service service instance
     * @param <T> service class
     */
    <T> void addServiceProvider(T service);

    /**
     * get service from map
     * @param serviceName name
     * @return service obj
     */
    Object getServiceProvider(String serviceName);

}
