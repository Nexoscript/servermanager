package com.nexoscript.servermanager.node.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerActionRunner {
    private final Map<String, MinecraftServerProcess> servers;

    public ServerActionRunner() {
        this.servers = new HashMap<>();
    }

    public void startServer(String name, String path, String jar) {
        if (servers.containsKey(name)) {
            System.out.println("Server '" + name + "' läuft bereits.");
            return;
        }

        try {
            MinecraftServerProcess server = new MinecraftServerProcess(name, path, jar);
            server.start();
            servers.put(name, server);
            System.out.println("Server '" + name + "' gestartet.");
        } catch (Exception e) {
            System.out.println("Fehler beim Starten des Servers '" + name + "': " + e.getMessage());
        }
    }

    public void stopServer(String name) {
        MinecraftServerProcess server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }

        server.stop();
        servers.remove(name);
        System.out.println("Server '" + name + "' gestoppt.");
    }

    public void openConsole(Scanner scanner, String name) {
        MinecraftServerProcess server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }
        server.console(scanner);
    }

    public void shutdownAllServers() {
        for (MinecraftServerProcess server : new ArrayList<>(servers.values())) {
            server.stop();
        }
        servers.clear();
        System.out.println("Alle Server gestoppt.");
    }

    public Map<String, MinecraftServerProcess> getServers() {
        return servers;
    }
}