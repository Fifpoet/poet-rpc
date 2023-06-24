package org.fifpoet.rpc;

import org.fifpoet.rpc.serializer.CommonSerializer;

public interface RpcServer {
    void start();
    <T> void publishService(Object service, Class<T> serviceClass);
    void setSerializer(CommonSerializer serializer);

}
