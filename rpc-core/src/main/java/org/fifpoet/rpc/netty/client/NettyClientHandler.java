package org.fifpoet.rpc.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.util.LogUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse<Object> msg) throws Exception {
        try {
            LogUtil.INFO().info(String.format("client receive : %s", msg));
            AttributeKey<RpcResponse<Object>> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LogUtil.ERROR().error("Netty client error:");
        cause.printStackTrace();
        ctx.close();
    }
}
