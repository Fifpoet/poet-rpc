package org.fifpoet.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.balancer.RoundRobinLoadBalancer;
import org.fifpoet.util.LogUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry{
    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            LogUtil.ERROR().error("error connecting to nacos: ", e);
            throw new RpcException(RpcErrorCode.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            LogUtil.ERROR().error("error register a service:", e);
            throw new RpcException(RpcErrorCode.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            // get all service provider(machine), and get the first.
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = new RoundRobinLoadBalancer().select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            LogUtil.ERROR().error("error lookup a service:", e);
        }
        return null;
    }
}
