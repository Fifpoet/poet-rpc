package org.fifpoet.rpc.transport.netty.client;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.ServiceConfig;
import org.fifpoet.rpc.RpcClient;
import org.fifpoet.util.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final String version;
    private final String impl;

    public RpcClientProxy(RpcClient rpcClient, String version, String impl) {
        this.rpcClient = rpcClient;
        this.version = version;
        this.impl = impl;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // req.ServiceName: name of Stub(iface)
        RpcRequest rpcRequest = new RpcRequest(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), version, impl);
        LogUtil.INFO().info("request param: {}", rpcRequest);
        return rpcClient.sendRequest(rpcRequest);
    }
}
