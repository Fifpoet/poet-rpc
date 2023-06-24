package org.fifpoet.rpc.provider;

import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.util.LogUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceProvider {

    // service map & set. both concurrent safe.
    // make them static, insure all instance share one register center
    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    @Override
    public synchronized  <T> void addServiceProvider (T service) {
        // get the whole name.   e.g. com.example.HelloService
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new RpcException(RpcErrorCode.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        //TODO register all interface->service into map.
        for(Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        LogUtil.INFO().info("service register. {}. service: {}", interfaces, serviceName);
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
