package org.fifpoet.rpc;

import org.fifpoet.entity.ServiceConfig;
import org.fifpoet.rpc.serializer.CommonSerializer;

public interface RpcServer {

    <T> void publishService(ServiceConfig config);

}
