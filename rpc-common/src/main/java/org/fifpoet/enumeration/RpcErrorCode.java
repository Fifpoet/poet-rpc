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
    UNKNOWN_PACKAGE_TYPE("unknown package type"),
    SERIALIZER_NOT_FOUND("serializer not found"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("failed to connect to service registry"),
    REGISTER_SERVICE_FAILED("注册服务失败");

    private final String message;
}
