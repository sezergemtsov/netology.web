package ru.netology;

import java.io.BufferedOutputStream;

public interface Handler {
    public void toHandle(BufferedOutputStream out);
}
