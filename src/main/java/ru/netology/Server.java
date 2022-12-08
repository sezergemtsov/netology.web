package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Server {
    protected ServerSocket socket;
    protected ConcurrentHashMap<String,ConcurrentHashMap<String,Handler>> handlers = new ConcurrentHashMap<>();
    ExecutorService service;
    public Server(int port) {
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        service = Executors.newFixedThreadPool(64);
    }
    public void start() {
        while (true) {
            try {
                service.submit(new ConnectionController(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void setNewHandler(String method, String path, Handler handler) {
        var pathMap = handlers.computeIfAbsent(method,k->new ConcurrentHashMap<>());
        pathMap.put(method,handler);
    }
}
