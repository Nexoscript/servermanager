package com.nexoscript.servermanager.node.server;

import com.nexoscript.servermanager.node.ServerManagerNode;
import com.nexoscript.servermanager.node.template.TemplateManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerActionRunner {
    private final ServerManagerNode node;
    private final TemplateManager templateManager;
    private final Map<String, MinecraftServerProcess> servers;

    public ServerActionRunner(ServerManagerNode node, TemplateManager templateManager) {
        this.node = node;
        this.templateManager = templateManager;
        this.servers = new HashMap<>();
    }

    public void createServer(Path path, String templateName) {
        try {
            if (!path.toFile().mkdirs()) {
                System.out.println("Konnte Pfad nicht erstellen.");
                return;
            }
            Path templatePath = this.templateManager.getTemplate(templateName);
            if (templatePath == null) {
                System.out.println("Template Pfad konnte nicht gefunden werden.");
                return;
            }
            System.out.println("[SERVER] Template-Path: " + templatePath);
            Files.walk(templatePath).forEach(source -> {
                try {
                    Path destination = path.resolve(templatePath.relativize(source));

                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                        return;
                    }

                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println("Fehler beim Erstellen des Servers mit Pfad '" + path + "': " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println("Fehler beim Erstellen des Servers mit Pfad '" + path.toString() + "': " + e.getMessage());
        }
    }

    public void startServer(String name, String path, String jar) {
        if (servers.containsKey(name)) {
            System.out.println("Server '" + name + "' läuft bereits.");
            return;
        }

        try {
            MinecraftServerProcess server = new MinecraftServerProcess(node, name, path, jar);
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