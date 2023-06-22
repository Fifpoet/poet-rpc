package org.fifpoet.rpc.registry;

/**
 * universal service register interface
 */
public interface ServiceRegistry {
    /**
     * register one service into registry
     * @param service service instance
     * @param <T> service class
     */
    <T> void register(T service);

    /**
     * get service from map
     * @param serviceName name
     * @return service obj
     */
    Object getService(String serviceName);

}
