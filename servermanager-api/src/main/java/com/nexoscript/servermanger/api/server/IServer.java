package com.nexoscript.servermanger.api.server;

import java.util.Scanner;

public interface IServer {
    void start();
    void stop();
    void console(Scanner scanner);
}
