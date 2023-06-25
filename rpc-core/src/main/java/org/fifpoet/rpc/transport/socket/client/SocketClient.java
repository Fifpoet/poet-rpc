package org.fifpoet.rpc.transport.socket.client;

import org.fifpoet.entity.RpcRequest;
import org.fifpoet.entity.RpcResponse;
import org.fifpoet.enumeration.RegistryCenterCode;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.RpcClient;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.util.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private final CommonSerializer serializer;
    private final ServiceRegistry registry;
    public SocketClient() {
        this.serializer = CommonSerializer.getByCode(SerializerCode.DEFAULT.getCode());
        this.registry = ServiceRegistry.getByCode(RegistryCenterCode.DEFAULT.getCode());
    }
    public SocketClient(CommonSerializer serializer, ServiceRegistry registry) {
        this.serializer = serializer;
        this.registry = registry;
    }

    @Override
    public Object sendRequest(RpcRequest request) {
        if(serializer == null) {
            LogUtil.ERROR().error("serializer not found");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress server = registry.lookupService(request.getInterfaceName());
        try (Socket socket = new Socket()) {
            socket.connect(server);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            Object obj = objectInputStream.readObject();
            RpcResponse<?> response = (RpcResponse<?>) obj;
            return response.getData();
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.ERROR().error("Client send request failed");
            return null;
        }
    }
}
