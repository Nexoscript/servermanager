package de.eztxm.servermanager;

import de.eztxm.servermanager.server.ServerActionRunner;

public class ServerManager {
    private static ServerActionRunner actionRunner;

    public static void main(String[] args) {
        actionRunner = new ServerActionRunner();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm wird beendet. Stoppe alle Server...");
            actionRunner.shutdownAllServers();
        }));
    }

    public static ServerActionRunner getActionRunner() {
        return actionRunner;
    }
}
