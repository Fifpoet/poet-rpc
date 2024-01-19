package org.fifpoet.rpc.transport.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.ResponseCode;
import org.fifpoet.rpc.endpoints.ServiceEndpoints;
import org.fifpoet.util.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * handle Netty request
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest req) throws Exception {
        try {
            LogUtil.INFO().info("netty receive req: {}", req);
            Object service = ServiceEndpoints.getServiceObject(req.getServiceName());
            Object result = handle(req, service);
            ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(req);
        }
    }

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            Method method;
            try {
                //TODO 获取method实例比较耗时，可以加入缓存
                method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            } catch (NoSuchMethodException e) {
                return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
            }
            result = method.invoke(service, rpcRequest.getParameters());
            LogUtil.INFO().info("service:{} invoke method success:{}", rpcRequest.getServiceName(), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.ERROR().error("invoke target method error：", e);
        } return result;
    }

}
