package org.fifpoet.test;

import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.registry.DefaultServiceRegistry;
import org.fifpoet.rpc.server.RpcServer;

/**
 * 测试用服务提供方（服务端）
 * @author ziyang
 */
public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer(new DefaultServiceRegistry());
        rpcServer.start(helloService, 9000);
    }

}
