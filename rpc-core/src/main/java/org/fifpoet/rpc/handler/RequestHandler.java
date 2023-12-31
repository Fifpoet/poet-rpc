package org.fifpoet.rpc.handler;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.ResponseCode;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ServiceNameUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    public Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            LogUtil.INFO().info("service:{} invoke method success:{}", ServiceNameUtil.getFullName(rpcRequest), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.ERROR().error("invoke target method error：", e);
        } return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            //TODO 获取method实例比较耗时，可以加入缓存
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD);
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
}
