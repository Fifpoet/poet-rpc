package org.fifpoet.test;

import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.transport.netty.server.NettyServer;
import org.fifpoet.rpc.provider.DefaultServiceRegistry;
import org.fifpoet.rpc.provider.ServiceProvider;

/**
 * 测试用服务提供方（服务端）
 * @author ziyang
 */
public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceProvider registry = new DefaultServiceRegistry();
        registry.addServiceProvider(helloService);
        RpcServer rpcServer = new NettyServer();
        rpcServer.start(helloService, 9000);
    }

}
