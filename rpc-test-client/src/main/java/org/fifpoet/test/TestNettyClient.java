package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcClientProxy;
import org.fifpoet.rpc.registry.NacosServiceRegistry;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.transport.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy(new NettyClient());
        HelloService helloService = proxy.getProxy(HelloService.class);
        String returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
