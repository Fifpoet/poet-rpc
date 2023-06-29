package org.fifpoet.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.fifpoet.entity.ServiceConfig;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.codec.CommonDecoder;
import org.fifpoet.rpc.codec.CommonEncoder;
import org.fifpoet.rpc.handler.AnnotationHandler;
import org.fifpoet.rpc.hook.ShutdownHook;
import org.fifpoet.rpc.provider.ServiceProvider;
import org.fifpoet.rpc.provider.ServiceProviderImpl;
import org.fifpoet.rpc.registry.NacosServiceRegistry;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ServiceNameUtil;

import java.net.InetSocketAddress;

public class NettyServer extends AnnotationHandler implements RpcServer {
    private final String host;
    private final int port;
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(SerializerCode.DEFAULT.getCode());
        // check annotation
        scanServices();
    }

    public NettyServer(String host, int port, CommonSerializer serializer) {
        this(host, port);
        this.serializer = serializer;
    }

    @Override
    public <T> void publishService(ServiceConfig config) {
        if(serializer == null) {
            LogUtil.ERROR().error("no serializer found");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(config);
        serviceRegistry.register(ServiceNameUtil.getFullInterfaceName(config), new InetSocketAddress(host, port));
        start();
    }

    public void start() {
        // add hook to shut down old service in center
        ShutdownHook.getShutdownHook().addClearAllHook();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            LogUtil.ERROR().error("Netty server start error: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
