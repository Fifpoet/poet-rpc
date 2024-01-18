package org.fifpoet.rpc.endpoints.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.strategy.balancer.ConsistentHashLoadBalance;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

public class NacosServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            LogUtil.ERROR().error("error register a service:", e);
            throw new RpcException(RpcErrorCode.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public List<Instance> getAllInstance(String serviceName) {
        try {
            // get all service provider(machine)
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            // filter unhealthy ins
            return instances.stream().filter(Instance::isHealthy).filter(Instance::isEnabled).collect(Collectors.toList());
        } catch (NacosException e) {
            LogUtil.ERROR().error("error lookup a service:", e);
        }
        return null;
    }
}
