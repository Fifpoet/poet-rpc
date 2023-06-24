package org.fifpoet.rpc;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.rpc.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
    void setSerializer(CommonSerializer serializer);
}
