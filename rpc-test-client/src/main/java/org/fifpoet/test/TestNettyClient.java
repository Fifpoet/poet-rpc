package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloResult;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcClientProxy;
import org.fifpoet.rpc.transport.netty.client.NettyClient;

public class TestNettyClient {
    public static void main(String[] args) {
        String serviceVersion = "";
        String impl = "second";
        RpcClientProxy proxy = new RpcClientProxy(new NettyClient(), serviceVersion, impl);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloResult returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
