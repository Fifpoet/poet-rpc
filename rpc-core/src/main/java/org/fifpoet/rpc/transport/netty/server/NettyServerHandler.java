package org.fifpoet.rpc.transport.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.fifpoet.rpc.server.RequestHandler;
import org.fifpoet.util.LogUtil;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.rpc.provider.DefaultServiceRegistry;
import org.fifpoet.rpc.provider.ServiceProvider;

/**
 * handle Netty request
 * @author ziyang
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final RequestHandler requestHandler;
    private static final ServiceProvider serviceProvider;

    static {
        requestHandler = new RequestHandler();
        serviceProvider = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest req) throws Exception {
        try {
            LogUtil.INFO().info("netty receive req: {}", req);
            String interfaceName = req.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(interfaceName);
            Object result = requestHandler.handle(req, service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtil.ERROR().error("error occurred handling Netty req:");
        cause.printStackTrace();
        ctx.close();
    }

}
