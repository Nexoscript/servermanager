package com.nexoscript.servermanager.node.console;

import java.nio.file.Path;
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
        System.out.println("Server-Manager gestartet. Verwenden Sie 'start', 'stop', 'console', 'template', oder 'exit'.");
        while (this.running) {
            String input = this.scanner.nextLine();
            String[] commandParts = input.split(" ", 4);
            if (commandParts.length < 1) {
                continue;
            }
            String command = commandParts[0].toLowerCase();
            switch (command) {
                case "create" -> {
                    if (commandParts.length < 3 || commandParts[1].isBlank() || commandParts[2].isBlank()) {
                        System.out.println("Verwendung: create <path> <template>");
                        continue;
                    }
                    this.actionRunner.createServer(Path.of(commandParts[1]), commandParts[2]);
                }
                case "start" -> {
                    if (commandParts.length < 4) {
                        System.out.println("Verwendung: start <servername> <path> <jar>");
                        continue;
                    }
                    this.actionRunner.startServer(commandParts[1], commandParts[2], commandParts[3]);
                }
                case "stop" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: stop <servername>");
                        continue;
                    }
                    this.actionRunner.stopServer(commandParts[1]);
                }
                case "console" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: console <servername>");
                        continue;
                    }
                    this.actionRunner.openConsole(this.scanner, commandParts[1]);
                }
                case "template" -> {
                    if (commandParts.length < 2) {
                        System.out.println("Verwendung: 'create', 'rename', 'delete' oder 'list'");
                        continue;
                    }
                    switch (commandParts[1].toLowerCase()) {
                        case "create" -> {
                            if (commandParts.length < 3 || commandParts[2].isBlank()) {
                                System.out.println("Verwendung: template create <name>");
                                continue;
                            }
                            this.templateManager.createTemplate(commandParts[2]);
                            System.out.println("Template " + commandParts[2] + " erstellt.");
                        }
                        case "rename" -> {
                            if (commandParts.length < 4 || commandParts[2].isBlank() || commandParts[3].isBlank()) {
                                System.out.println("Verwendung: template rename <name> <newName>");
                                continue;
                            }
                            this.templateManager.renameTemplate(commandParts[2], commandParts[3]);
                            System.out.println("Template " + commandParts[2] + " zu " + commandParts[3] + " umbenannt.");
                        }
                        case "delete" -> {
                            if (commandParts.length < 3 || commandParts[2].isBlank()) {
                                System.out.println("Verwendung: template delete <name>");
                                continue;
                            }
                            this.templateManager.deleteTemplate(commandParts[2]);
                            System.out.println("Template " + commandParts[2] + " gelöscht.");
                        }
                        case "list" -> {
                            if (this.templateManager.templates().isEmpty()) {
                                System.out.println("Keine Templates gefunden.");
                                continue;
                            }
                            this.templateManager.templates().forEach((name, path) -> System.out.printf("- %s -> %s%n", name, path.toString()));
                        }
                    }
                }
                case "exit" -> this.running = false;
                default -> System.out.println("Unbekannter Befehl: " + command);
            }
        }
        this.scanner.close();
    }
}
