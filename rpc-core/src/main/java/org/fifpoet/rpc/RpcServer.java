package org.fifpoet.rpc;

import org.fifpoet.rpc.serializer.CommonSerializer;

public interface RpcServer {
    <T> void publishService(Object service, Class<T> serviceClass);

}
