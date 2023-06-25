package org.fifpoet.test;

import org.fifpoet.api.HelloService;
import org.fifpoet.rpc.annotation.RpcServiceScan;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.transport.socket.server.SocketServer;

/**
 * 测试用服务提供方（服务端）
 * @author ziyang
 */
@RpcServiceScan
public class TestSocketServer {

    public static void main(String[] args) {
        SocketServer server = new SocketServer("127.0.0.1", 9999);
    }

}
