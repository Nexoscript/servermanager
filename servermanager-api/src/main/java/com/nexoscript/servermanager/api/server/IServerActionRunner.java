package com.nexoscript.servermanager.api.server;

import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;

public interface IServerActionRunner {
    void createServer(Path path, String templateName);
    void startServer(String name, String path, String jar);
    void stopServer(String name);
    void openConsole(Scanner scanner, String name);
    void shutdownAllServers();
    Map<String, IServer> servers();
}
