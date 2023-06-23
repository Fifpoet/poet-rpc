package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcClientProxy;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.netty.client.NettyClient;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy(new NettyClient("127.0.0.1", 9000));
        HelloService helloService = proxy.getProxy(HelloService.class);
        String returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
