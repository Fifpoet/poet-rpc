package org.fifpoet.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.fifpoet.entity.Endpoint;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.ShutdownHook;
import org.fifpoet.rpc.annotation.RpcService;
import org.fifpoet.rpc.annotation.RpcServiceScan;
import org.fifpoet.rpc.codec.CommonDecoder;
import org.fifpoet.rpc.codec.CommonEncoder;
import org.fifpoet.rpc.endpoints.ServiceEndpoints;
import org.fifpoet.rpc.endpoints.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.rpc.strategy.balancer.ConsistentHashLoadBalance;
import org.fifpoet.rpc.strategy.router.IPRouter;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ReflectUtil;

import java.util.Set;

public class NettyServer {
    private final String host;
    private final int port;
    private final ServiceEndpoints endpoints;
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.endpoints = new ServiceEndpoints();
        this.serializer = CommonSerializer.getByCode(SerializerCode.DEFAULT.getCode());
        // check annotation
        scanServices(host, port);
    }

    public NettyServer(String host, int port, CommonSerializer serializer) {
        this(host, port);
        this.serializer = serializer;
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

    public void scanServices(String host, int port)  {
        // get the bottom of invoke stack
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(mainClassName);
            if(!startClass.isAnnotationPresent(RpcServiceScan.class)) {
                LogUtil.INFO().error("启动类缺少 @RpcServiceScan 注解");
                throw new RpcException(RpcErrorCode.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            LogUtil.INFO().error("出现未知错误");
            throw new RpcException(RpcErrorCode.UNKNOWN_ERROR);
        }

        String basePackage = startClass.getAnnotation(RpcServiceScan.class).value();
        if("".equals(basePackage)) {
            basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
        }
        // traverse the package to find the annotated class
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(RpcService.class)) {
                //instance
                Object implInstance;
                try {
                    implInstance = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                RpcService anno = clazz.getAnnotation(RpcService.class);
                endpoints.addAndCacheEndpoints(new Endpoint(anno.group(), implInstance, host, port));
                start();
            }
        }
    }
}
