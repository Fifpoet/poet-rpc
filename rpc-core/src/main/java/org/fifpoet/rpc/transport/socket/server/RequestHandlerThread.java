package org.fifpoet.rpc.transport.socket.server;

import lombok.AllArgsConstructor;
import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.rpc.provider.ServiceProvider;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.rpc.handler.RequestHandler;
import org.fifpoet.util.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * single thread to handle one request
 */
@AllArgsConstructor
public class RequestHandlerThread implements Runnable{
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceProvider serviceProvider;
    private CommonSerializer serializer;

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // get method and invoke by reflection
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getServiceProvider(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.ERROR().error("remote method invoke error：", e);
        }
    }
}
