package com.nexoscript.servermanager.node.console;

import java.util.Scanner;

import com.nexoscript.servermanager.node.server.ServerActionRunner;
import com.nexoscript.servermanager.node.template.TemplateManager;

public class Console {
    private final TemplateManager templateManager;
    private final ServerActionRunner actionRunner;
    private final Scanner scanner;

    private boolean running;

    public Console(TemplateManager templateManager, ServerActionRunner actionRunner) {
        this.templateManager = templateManager;
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
                case "create-template" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: create-template <name>");
                        break;
                    }
                    this.templateManager.createTemplate(commandParts[1]);
                    System.out.println("Template " + commandParts[1] + " erstellt.");
                }
                case "rename-template" -> {
                    if (commandParts.length < 3) {
                        System.out.println("Verwendung: rename-template <name> <newName>");
                        break;
                    }
                    this.templateManager.renameTemplate(commandParts[1], commandParts[2]);
                    System.out.println("Template " + commandParts[1] + " zu " + commandParts[2] + " umbenannt.");
                }
                case "delete-template" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: delete-template <name>");
                        break;
                    }
                    this.templateManager.deleteTemplate(commandParts[1]);
                    System.out.println("Template " + commandParts[1] + " gelöscht.");
                }
                case "exit" -> this.running = false;
                default -> System.out.println("Unbekannter Befehl: " + command);
            }
        }
        this.scanner.close();
    }
}
