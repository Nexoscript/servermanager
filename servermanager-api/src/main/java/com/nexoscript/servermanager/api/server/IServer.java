package com.nexoscript.servermanager.api.server;

import java.util.Scanner;

public interface IServer {
    void start();
    void stop();
    void console(Scanner scanner);
}
