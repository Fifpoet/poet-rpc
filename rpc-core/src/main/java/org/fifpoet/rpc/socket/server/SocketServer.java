package org.fifpoet.rpc.socket.server;

import org.fifpoet.rpc.RpcServer;
import org.fifpoet.rpc.registry.ServiceRegistry;
import org.fifpoet.rpc.server.RequestHandler;
import org.fifpoet.util.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;

    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start(Object service, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                serviceRegistry.register(service);
                LogUtil.INFO().info("accept client, IP：" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(socket, serviceRegistry, new RequestHandler()));
            }
            threadPool.shutdownNow();
        } catch (IOException e) {
            LogUtil.ERROR().error("connection error：", e);
        }
    }
}
