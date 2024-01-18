package org.fifpoet.util;

import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.exception.RpcException;

public class ServiceNameUtil {
    public static String getInterfaceNameFromStub(Object stub) {
        Class<?>[] interfaces = stub.getClass().getInterfaces();
        if(interfaces.length != 1) {
            throw new RpcException(RpcErrorCode.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        return interfaces[0].getCanonicalName();
    }
}
