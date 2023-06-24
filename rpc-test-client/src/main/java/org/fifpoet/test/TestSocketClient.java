package org.fifpoet.test;

import org.fifpoet.api.HelloParam;
import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcClientProxy;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.transport.netty.client.NettyClient;
import org.fifpoet.rpc.transport.socket.client.SocketClient;

public class TestSocketClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String returnVar = helloService.hello(new HelloParam(1, "hhh, what u doing"));
        System.out.println(returnVar);
    }
}
