package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloResult;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.transport.netty.client.RpcClientProxy;
import org.fifpoet.rpc.registry.NacosServiceRegistry;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.transport.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        String serviceVersion = "0";
        String impl = "";
        SocketClient client = new SocketClient(new KryoSerializer(), new NacosServiceRegistry());
        RpcClientProxy proxy = new RpcClientProxy(client, serviceVersion, impl);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloResult returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
