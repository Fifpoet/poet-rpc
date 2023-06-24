package org.fifpoet.rpc.transport.socket.client;


import org.fifpoet.entity.RpcRequest;
import org.fifpoet.rpc.RpcClient;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.util.LogUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private final String host;
    private final int port;
    private CommonSerializer serializer;

    public SocketClient (String host, int port) {
        this.host = host;
        this.port = port;
    }
    @Override
    public Object sendRequest(RpcRequest request) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LogUtil.ERROR().error("Client send request failed");
            return null;
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
