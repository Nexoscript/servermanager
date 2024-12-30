package de.eztxm.servermanager.node;

import de.eztxm.servermanager.node.console.Console;
import de.eztxm.servermanager.node.server.ServerActionRunner;

public class ServerManagerNode {
    private static ServerActionRunner actionRunner;
    private static Console console;

    public static void main(String[] args) {
        actionRunner = new ServerActionRunner();
        shutdownHook();
        console = new Console(actionRunner);
        console.start();
    }

    public static void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm wird beendet. Stoppe alle Server...");
            actionRunner.shutdownAllServers();
        }));
    }

    public static Console getConsole() {
        return console;
    }

    public static ServerActionRunner getActionRunner() {
        return actionRunner;
    }
}