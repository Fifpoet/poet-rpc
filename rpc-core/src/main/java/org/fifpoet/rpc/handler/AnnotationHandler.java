package org.fifpoet.rpc.handler;

import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.annotation.RpcService;
import org.fifpoet.rpc.annotation.RpcServiceScan;
import org.fifpoet.util.LogUtil;
import org.fifpoet.util.ReflectUtil;

import java.util.Set;

public abstract class AnnotationHandler implements RpcServer {
    public void scanServices() {
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
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        for(Class<?> clazz : classSet) {
            if(clazz.isAnnotationPresent(RpcService.class)) {
                String serviceName = clazz.getAnnotation(RpcService.class).name();
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    LogUtil.INFO().error("创建 " + clazz + " 时有错误发生");
                    continue;
                }
                if("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface: interfaces){
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }
    }

}
