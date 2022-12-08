package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


public class ConnectionController implements Runnable {

    ServerSocket socket;
    ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers;

    final AtomicReference<Request> request;

    protected ConnectionController(ServerSocket socket, ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;
        request = new AtomicReference<>(new Request());
    }

    @Override
    public void run() {
        try (
                final var socket = this.socket.accept();
                final BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            final var limit = 4096;

            in.mark(0);
            final var buffer = new byte[limit];
            final var read = in.read(buffer, 0, limit);

            // ищем request line
            final var requestLineDelimiter = new byte[]{'\r', '\n'};
            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
            if (requestLineEnd == -1) {
                badRequest(out);
                return;
            }
            // читаем request line
            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                badRequest(out);
                return;
            }
            final var method = requestLine[0];
            request.get().setMethod(method);
            final var pathLine = requestLine[1];
            if (!pathLine.startsWith("/")) {
                badRequest(out);
                return;
            }
            request.get().setPath(pathLine);
            if (!handlers.containsKey(request.get().getMethod())) {
                notFound(out);
                return;
            }
            if (!handlers.get(method).containsKey(request.get().getPath())) {
                notFound(out);
                return;
            }
            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
            if (headersEnd == -1) {
                badRequest(out);
                return;
            }
            // отматываем на начало буфера
            in.reset();
            // пропускаем requestLine
            in.skip(headersStart);
            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
            request.get().setHeaders(headers);
            // для GET тела нет
            if (!Objects.equals(method, "GET")) {
                in.skip(headersDelimiter.length);
                // вычитываем Content-Length, чтобы прочитать body
                final var contentLength = extractHeader(headers, "Content-Length");
                if (contentLength.isPresent()) {
                    final var length = Integer.parseInt(contentLength.get());
                    final var bodyBytes = in.readNBytes(length);
                    final var body = new String(bodyBytes);
                    request.get().setBody(body);
                    if (extractHeader(headers, "Content-Type").get().equals("application/x-www-form-urlencoded")) {
                        request.get().setxWWWFormEncodedParams();
                    } else if (extractHeader(headers, "Content-Type").get().contains("multipart/form-data")) {
                        request.get().setMultipartFormDataParams();
                    }
                }
            }
            handlers.get(request.get().getMethod()).get(request.get().getPath()).toHandle(out, request);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private static void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    // from Google guava with modifications
    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
}
