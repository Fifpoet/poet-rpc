package org.fifpoet.rpc;

import org.fifpoet.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
