package org.fifpoet.rpc.transport.netty.client;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.util.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    private final NettyClient rpcClient;

    public RpcClientProxy(NettyClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // req.ServiceName: name of Stub(iface)
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        LogUtil.INFO().info("request param: {}", rpcRequest);
        return rpcClient.sendRequest(rpcRequest);
    }
}
