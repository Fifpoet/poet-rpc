package org.fifpoet.rpc.provider;

import org.fifpoet.entity.ServiceConfig;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ServiceNameUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * reserve local services
 */
public class ServiceProviderImpl implements ServiceProvider {

    // service map & set. both concurrent safe.
    // make them static, insure all instance share one register center
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public synchronized  <T> void addServiceProvider (ServiceConfig config) {
        // local Service Name -> impl obj
        // remote Register Center -> iface name
        String serviceName = ServiceNameUtil.getFullInterfaceName(config);
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, config.getService());
        LogUtil.INFO().info("service register. {}. service: {}", serviceName, config.getService());
    }

    @Override
    public synchronized Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RpcException(RpcErrorCode.SERVICE_NOT_FOUND);
        }
        return service;
    }


}
