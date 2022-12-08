package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
  public static void main(String[] args) {
    int port = 9999;

    Server server = new Server(port);
    server.setNewHandler("GET", "/messages", new Handler(){
      @Override
      public void toHandle(BufferedOutputStream out) {
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


