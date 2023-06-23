package org.fifpoet.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcErrorCode {
    SERVICE_INVOCATION_FAILURE("service invocation error!"),
    SERVICE_NOT_FOUND("invoked service not found!"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("register a service without implementing any interface!"),
    UNKNOWN_PROTOCOL("unknown protocol"),
    UNKNOWN_SERIALIZER("unknown en/decoder"),
    UNKNOWN_PACKAGE_TYPE("unknown package type");

    private final String message;
}