package org.fifpoet.test;

import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.netty.server.NettyServer;
import org.fifpoet.rpc.registry.DefaultServiceRegistry;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 * @author ziyang
 */
public class TestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        RpcServer rpcServer = new NettyServer();
        rpcServer.start(helloService, 9000);
    }

}
