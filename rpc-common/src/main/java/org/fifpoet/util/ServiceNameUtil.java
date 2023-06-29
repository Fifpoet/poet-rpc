package org.fifpoet.util;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.ServiceConfig;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;

import java.util.Arrays;

public class ServiceNameUtil {
    public static String getFullName(String className, String version, String impl) {
        StringBuilder res = new StringBuilder(className);
        // if impl in annotation is not null, append it.
        if (impl != null && impl.length() != 0) {
            res.append("-").append(impl);
        }
        res.append("-");
        if (version == null || version.equals("")) {
            res.append("0");
        }else {
            res.append(version);
        }
        return res.toString();
    }
    public static String getFullName(ServiceConfig config) {
        return getFullName(config.getService().toString(), config.getVersion(), config.getImpl());
    }
    public static String getFullName(RpcRequest request) {
        return getFullName(request.getServiceName(), request.getVersion(), request.getImpl());
    }

    public static String getFullInterfaceName(ServiceConfig config) {
        Object implObj = config.getService();
        // implement has none or more than one interface
        Class<?>[] interfaces = implObj.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new RpcException(RpcErrorCode.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        return ServiceNameUtil.getFullName(interfaces[0].getCanonicalName(), config.getVersion(), config.getImpl());
    }

}
