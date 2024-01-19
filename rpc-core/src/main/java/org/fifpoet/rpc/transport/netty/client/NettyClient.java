package org.fifpoet.rpc.transport.netty.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.rpc.codec.CommonDecoder;
import org.fifpoet.rpc.codec.CommonEncoder;
import org.fifpoet.rpc.endpoints.ServiceEndpoints;
import org.fifpoet.rpc.endpoints.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.util.LogUtil;

import java.net.InetSocketAddress;

/**
 * use netty to send packet to Server.
 */
public class NettyClient {

    private static final Bootstrap bootstrap;
    private final CommonSerializer serializer; //编码策略
    private final ServiceEndpoints endpoints;
    private final ServiceRegistry registry;

    public NettyClient() {
        this.serializer = CommonSerializer.getDefaultSerializer();
        this.registry = ServiceRegistry.getDefaultRegistry();
        this.endpoints = new ServiceEndpoints();
    }

    //init EventLoopGroup, bind to bootstrap
    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new KryoSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    public Object sendRequest(RpcRequest rpcRequest) {
        Instance instance = endpoints.filterEndpointWithStrategy(rpcRequest.getServiceName(), rpcRequest);
        InetSocketAddress server = new InetSocketAddress(instance.getIp(), instance.getPort());
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
                channel.closeFuture().sync();
                AttributeKey<RpcResponse<Object>> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse<Object> rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            LogUtil.ERROR().error("sendRequest: send request or get response failed: ", e);
        }
        return null;
    }
}
