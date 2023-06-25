package org.fifpoet.rpc.transport.socket.server;

import org.fifpoet.enumeration.RegistryCenterCode;
import org.fifpoet.enumeration.RpcErrorCode;
import org.fifpoet.enumeration.SerializerCode;
import org.fifpoet.exception.RpcException;
import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.provider.ServiceProvider;
import org.fifpoet.rpc.provider.ServiceProviderImpl;
import org.fifpoet.rpc.registry.NacosServiceRegistry;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.serializer.CommonSerializer;
import org.fifpoet.rpc.serializer.KryoSerializer;
import org.fifpoet.rpc.handler.RequestHandler;
import org.fifpoet.util.LogUtil;

import javax.xml.ws.Service;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    private final String host;
    private final int port;
    private final CommonSerializer serializer;
    private final RequestHandler requestHandler;

    private final ServiceRegistry serviceRegistry;

    public SocketServer(String host, int port) {
        this.serializer = CommonSerializer.getByCode(SerializerCode.DEFAULT.getCode());
        this.serviceRegistry = ServiceRegistry.getByCode(RegistryCenterCode.DEFAULT.getCode());
        this.serviceProvider = new ServiceProviderImpl();
        this.requestHandler = new RequestHandler();
        this.host = host;
        this.port = port;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }
    public SocketServer(String host, int port, CommonSerializer serializer, ServiceRegistry registry) {
        this.serializer = serializer;
        this.serviceRegistry = registry;
        this.serviceProvider = new ServiceProviderImpl();
        this.requestHandler = new RequestHandler();
        this.host = host;
        this.port = port;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            LogUtil.ERROR().error("no serializer found");
            throw new RpcException(RpcErrorCode.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                LogUtil.INFO().info("accept client, IP：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceProvider, serializer));
            }
            threadPool.shutdownNow();
        } catch (IOException e) {
            LogUtil.ERROR().error("connection error：", e);
        }
    }
}
