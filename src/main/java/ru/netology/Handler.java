package ru.netology;

import java.io.BufferedOutputStream;
import java.util.concurrent.atomic.AtomicReference;

public interface Handler {
    public void toHandle(BufferedOutputStream out, AtomicReference<Request> request);
}
