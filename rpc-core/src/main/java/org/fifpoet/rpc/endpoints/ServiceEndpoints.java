package org.fifpoet.rpc.endpoints;

import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.entity.Endpoint;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.rpc.endpoints.registry.ServiceRegistry;
import org.fifpoet.rpc.strategy.balancer.LoadBalancer;
import org.fifpoet.rpc.strategy.router.CommonRouter;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ServiceNameUtil;
import org.springframework.cglib.core.Local;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * reserve local services
 */
public class ServiceEndpoints {

    private ServiceRegistry registry;
    private LoadBalancer balancer;
    private CommonRouter router;
    private static Map<String, Object> serviceObjectMap = new ConcurrentHashMap<>();

    public ServiceEndpoints() {
        this.registry = ServiceRegistry.getDefaultRegistry();
        this.balancer = LoadBalancer.getDefaultBalancer();
        this.router = CommonRouter.getDefaultRouter();
    }
    public ServiceEndpoints(ServiceRegistry registry, LoadBalancer balancer, CommonRouter router) {
        this.registry = registry;
        this.balancer = balancer;
        this.router = router;
    }

    /**
     * register one service into local registry
     * @param endpoint service config, contain service instance
     */
    public void addAndCacheEndpoints(Endpoint endpoint) {
        String serviceName = ServiceNameUtil.getInterfaceNameFromStub(endpoint.getStub());
        this.registry.register(serviceName, new InetSocketAddress(endpoint.getHost(), endpoint.getPort()));
        serviceObjectMap.put(serviceName, endpoint.getStub());
        LogUtil.INFO().info("service register. {}. service: {}", serviceName, endpoint.getStub());
    }

    public static Object getServiceObject(String name) {
        return serviceObjectMap.get(name);
    }

    /**
     * get service from map
     * @param serviceName name
     * @return service obj
     */
    public Instance filterEndpointWithStrategy(String serviceName, RpcRequest req) {
        List<Instance> origin = registry.getAllInstance(serviceName);
        List<Instance> routed = router.route(origin);
        return balancer.select(routed, req);
    }

}
