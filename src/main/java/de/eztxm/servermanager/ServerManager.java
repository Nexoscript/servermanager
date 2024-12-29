package de.eztxm.servermanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ServerManager {
    private static final Map<String, MinecraftServer> servers = new HashMap<>();
    private static boolean running = true;
    private static Scanner scanner;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm wird beendet. Stoppe alle Server...");
            shutdownAllServers();
        }));
        scanner = new Scanner(System.in);
        System.out.println("Multi-Server Manager gestartet. Verwenden Sie 'start', 'stop', 'console', oder 'exit'.");
        while (running) {
            String input = scanner.nextLine();
            String[] commandParts = input.split(" ", 4);

            if (commandParts.length < 1)
                continue;

            String command = commandParts[0].toLowerCase();

            switch (command) {
                case "start" -> {
                    if (commandParts.length < 4) {
                        System.out.println("Verwendung: start <servername> <path> <jar>");
                        break;
                    }
                    startServer(commandParts[1], commandParts[2], commandParts[3]);
                }
                case "stop" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: stop <servername>");
                        break;
                    }
                    stopServer(commandParts[1]);
                }
                case "console" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: console <servername>");
                        break;
                    }
                    openConsole(commandParts[1]);
                }
                case "exit" -> {
                    shutdownAllServers();
                    running = false;
                }
                default -> System.out.println("Unbekannter Befehl: " + command);
            }
        }

        scanner.close();
        System.out.println("Programm beendet.");
    }

    private static void startServer(String name, String path, String jar) {
        if (servers.containsKey(name)) {
            System.out.println("Server '" + name + "' läuft bereits.");
            return;
        }

        try {
            MinecraftServer server = new MinecraftServer(name, path, jar);
            server.start();
            servers.put(name, server);
            System.out.println("Server '" + name + "' gestartet.");
        } catch (Exception e) {
            System.out.println("Fehler beim Starten des Servers '" + name + "': " + e.getMessage());
        }
    }

    private static void stopServer(String name) {
        MinecraftServer server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }

        server.stop();
        servers.remove(name);
        System.out.println("Server '" + name + "' gestoppt.");
    }

    private static void openConsole(String name) {
        MinecraftServer server = servers.get(name);
        if (server == null) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }

        server.console(scanner);
    }

    private static void shutdownAllServers() {
        for (MinecraftServer server : servers.values()) {
            server.stop();
        }
        servers.clear();
        System.out.println("Alle Server gestoppt.");
    }
}
