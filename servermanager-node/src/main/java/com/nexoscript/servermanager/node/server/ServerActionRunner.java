package com.nexoscript.servermanager.node.server;

import com.nexoscript.servermanger.api.IServerManagerNode;
import com.nexoscript.servermanger.api.server.IServer;
import com.nexoscript.servermanger.api.server.IServerActionRunner;
import com.nexoscript.servermanger.api.template.ITemplateManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class ServerActionRunner implements IServerActionRunner {
    private final IServerManagerNode node;
    private final ITemplateManager templateManager;
    private final Map<String, IServer> servers;

    public ServerActionRunner(IServerManagerNode node, ITemplateManager templateManager) {
        this.node = node;
        this.templateManager = templateManager;
        this.servers = new HashMap<>();
    }

    @Override
    public void createServer(Path path, String templateName) {
            if (!path.toFile().mkdirs()) {
                System.out.println("Konnte Pfad nicht erstellen.");
                return;
            }
            Path templatePath = this.templateManager.templatePath(templateName);
            if (templatePath == null) {
                System.out.println("Template Pfad konnte nicht gefunden werden.");
                return;
            }
            System.out.println("[SERVER] Template-Path: " + templatePath);
        try (Stream<Path> files = Files.walk(templatePath)) {
            files.forEach(source -> {
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
            System.out.println("Fehler beim Erstellen des Servers mit Pfad '" + path + "': " + e.getMessage());
        }
    }

    @Override
    public void startServer(String name, String path, String jar) {
        if (servers.containsKey(name)) {
            System.out.println("Server '" + name + "' läuft bereits.");
            return;
        }

        try {
            IServer server = new Server(node, name, path, jar);
            server.start();
            servers.put(name, server);
            System.out.println("Server '" + name + "' gestartet.");
        } catch (Exception e) {
            System.out.println("Fehler beim Starten des Servers '" + name + "': " + e.getMessage());
        }
    }

    @Override
    public void stopServer(String name) {
        IServer server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }

        server.stop();
        servers.remove(name);
        System.out.println("Server '" + name + "' gestoppt.");
    }

    @Override
    public void openConsole(Scanner scanner, String name) {
        IServer server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }
        server.console(scanner);
    }

    @Override
    public void shutdownAllServers() {
        for (IServer server : new ArrayList<>(servers.values())) {
            server.stop();
        }
        servers.clear();
        System.out.println("Alle Server gestoppt.");
    }

    @Override
    public Map<String, IServer> servers() {
        return servers;
    }
}