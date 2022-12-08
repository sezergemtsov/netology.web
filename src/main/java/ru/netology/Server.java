package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Server {
    ServerSocket socket;
    int port;
    protected ConcurrentHashMap<String,ConcurrentHashMap<String,Handler>> handlers = new ConcurrentHashMap<>();
    ExecutorService service;
    public Server(int port) {
        this.port = port;
    }
    public void start() {
        try {
            socket = new ServerSocket(port);
            service = Executors.newFixedThreadPool(64);
            System.out.println("Server started with port: " + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            service.submit(new ConnectionController(this.socket, this.handlers));
        }
    }
    public void setNewHandler(String method, String path, Handler handler) {
        var pathMap = handlers.computeIfAbsent(method,k->new ConcurrentHashMap<>());
        pathMap.put(path,handler);
    }
}
