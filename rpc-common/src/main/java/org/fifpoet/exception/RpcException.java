package org.fifpoet.exception;

import org.fifpoet.enumeration.RpcErrorCode;

/**
 * RPC Runtime exception
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorCode error) {
        super(error.getMessage());
    }
    public RpcException(RpcErrorCode error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

}
