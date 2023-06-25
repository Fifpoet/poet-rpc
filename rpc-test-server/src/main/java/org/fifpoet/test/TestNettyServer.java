package org.fifpoet.test;

import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.annotation.RpcServiceScan;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.transport.netty.server.NettyServer;

/**
 * 测试用服务提供方（服务端）
 * @author ziyang
 */
@RpcServiceScan
public class TestNettyServer {

    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9000, new KryoSerializer());
    }

}
