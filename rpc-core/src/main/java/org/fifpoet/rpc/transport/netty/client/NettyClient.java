package org.fifpoet.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.RpcClient;
import org.fifpoet.rpc.codec.CommonDecoder;
import org.fifpoet.rpc.codec.CommonEncoder;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.util.LogUtil;

import java.net.InetSocketAddress;

public class NettyClient implements RpcClient {

    private static final Bootstrap bootstrap;
    private CommonSerializer serializer;
    private ServiceRegistry registry;
    public NettyClient(CommonSerializer serializer, ServiceRegistry registry) {
        this.serializer = serializer;
        this.registry = registry;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void setRegistry(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            LogUtil.ERROR().error("serializer not found");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress server = registry.lookupService(rpcRequest.getInterfaceName());
        try {
            ChannelFuture connFuture = bootstrap.connect(server).sync();
            LogUtil.INFO().info("client connected to host {}:{}", server.getHostName(), server.getPort());
            Channel channel = connFuture.channel();
            if(channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(sendMsgFuture -> {
                    if(sendMsgFuture.isSuccess()) {
                        LogUtil.INFO().info(String.format("client send msg: %s", rpcRequest));
                    } else {
                        LogUtil.ERROR().error("Client send request failed: ", sendMsgFuture.cause());
                    }
                });
                Thread.sleep(1000);
                channel.closeFuture().sync();
                AttributeKey<RpcResponse<Object>> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse<Object> rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            LogUtil.ERROR().error("send request or get response failed: ", e);
        }
        return null;
    }
}
