package org.fifpoet.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.RegistryCenterCode;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.enumeration.SerializerCode;
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
    private final CommonSerializer serializer;
    private final ServiceRegistry registry;

    public NettyClient() {
        this.serializer = CommonSerializer.getByCode(SerializerCode.DEFAULT.getCode());
        this.registry = ServiceRegistry.getByCode(RegistryCenterCode.DEFAULT.getCode());
    }
    public NettyClient(CommonSerializer serializer, ServiceRegistry registry) {
        this.serializer = serializer;
        this.registry = registry;
    }

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

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            LogUtil.ERROR().error("serializer not found");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress server = registry.lookupService(getFullServiceName(rpcRequest));
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
            LogUtil.ERROR().error("send request or get response failed: ", e);
        }
        return null;
    }

    private String getFullServiceName(RpcRequest req) {
        StringBuilder res = new StringBuilder(req.getServiceName());
        // if impl in annotation is not null, append it.
        String impl = req.getImpl();
        if (impl != null && impl.length() != 0) {
            res.append("-").append(impl);
        }
        res.append("-");
        String version = req.getVersion();
        if (version == null || version.equals("")) {
            res.append("0");
        }else {
            res.append(version);
        }
        return res.toString();
    }
}
