package org.fifpoet.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.balancer.RandomLoadBalancer;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.NacosUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceRegistry implements ServiceRegistry{

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
    public InetSocketAddress lookupService(String serviceName) {
        try {
            // get all service provider(machine), and get the first.
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            Instance instance = new RandomLoadBalancer().select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            LogUtil.ERROR().error("error lookup a service:", e);
        }
        return null;
    }
}
