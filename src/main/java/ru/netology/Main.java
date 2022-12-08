package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
  public static void main(String[] args) {
    int port = 9999;

    Server server = new Server(port);
    server.setNewHandler("GET", "/messages", new Handler(){
      @Override
      public void toHandle(BufferedOutputStream out, AtomicReference<Request> request) {
        if (request.get().getMethod() != null) {
          System.out.println(request.get().getMethod());
        }
        if (request.get().getPath() != null) {
          System.out.println(request.get().getPath());
        }
        if (!request.get().getQueryParams().isEmpty()) {
          request.get().getQueryParams().forEach(x->{
            System.out.print(x.getName()+" = ");
            System.out.print(x.getValue()+"\r\n");
          });
        }
        try {
          out.write((
                  "HTTP/1.1 200 Ok\r\n" +
                          "Content-Length: 0\r\n" +
                          "Connection: close\r\n" +
                          "\r\n"
          ).getBytes());
          out.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    server.setNewHandler("POST", "/messages", new Handler() {
      @Override
      public void toHandle(BufferedOutputStream out, AtomicReference<Request> request) {
        if (request.get().getMethod() != null) {
          System.out.println(request.get().getMethod());
        }
        if (request.get().getPath() != null) {
          System.out.println(request.get().getPath());
        }
        if (request.get().getxWWWFormEncodedParams() != null) {
          request.get().getxWWWFormEncodedParams().forEach(x->{
            System.out.print(x.getName()+" = ");
            System.out.print(x.getValue()+"\r\n");
          });
        }
        if (request.get().getMultipartFormDataParams() != null) {
          request.get().getMultipartFormDataParams().forEach(System.out::println);
        }
        try {
          out.write((
                  "HTTP/1.1 200 Ok\r\n" +
                          "Content-Length: 0\r\n" +
                          "Connection: close\r\n" +
                          "\r\n"
          ).getBytes());
          out.flush();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });
    server.start();
  }
}


