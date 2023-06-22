package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.client.RpcClientProxy;
import org.fifpoet.util.LogUtil;

public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
