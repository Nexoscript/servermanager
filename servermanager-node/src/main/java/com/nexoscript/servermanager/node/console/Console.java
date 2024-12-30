package com.nexoscript.servermanager.node.console;

import java.util.Scanner;

import com.nexoscript.servermanager.node.server.ServerActionRunner;

public class Console {
    private final ServerActionRunner actionRunner;
    private final Scanner scanner;

    private boolean running;

    public Console(ServerActionRunner actionRunner) {
        this.actionRunner = actionRunner;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        System.out.println("Multi-Server Manager gestartet. Verwenden Sie 'start', 'stop', 'console', oder 'exit'.");
        while (this.running) {
            String input = this.scanner.nextLine();
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
                    this.actionRunner.startServer(commandParts[1], commandParts[2], commandParts[3]);
                }
                case "stop" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: stop <servername>");
                        break;
                    }
                    this.actionRunner.stopServer(commandParts[1]);
                }
                case "console" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: console <servername>");
                        break;
                    }
                    this.actionRunner.openConsole(this.scanner, commandParts[1]);
                }
                case "exit" -> this.running = false;
                default -> System.out.println("Unbekannter Befehl: " + command);
            }
        }
        this.scanner.close();
    }
}
