package com.nexoscript.servermanager.node;

import com.nexoscript.servermanager.node.console.Console;
import com.nexoscript.servermanager.node.server.ServerActionRunner;
import com.nexoscript.servermanager.node.template.TemplateManager;
import com.nexoscript.servermanger.api.IServerManager;

public class ServerManagerNode implements IServerManager {
    private final TemplateManager templateManager;
    private final ServerActionRunner actionRunner;

    private final Console console;

    public static void main(String[] args) {
        new ServerManagerNode();
    }

    public ServerManagerNode() {
        this.templateManager = new TemplateManager();
        this.actionRunner = new ServerActionRunner(this);
        this.shutdownHook();
        this.console = new Console(this.templateManager, this.actionRunner);
        this.console.start();
    }

    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm wird beendet. Stoppe alle Server...");
            this.actionRunner.shutdownAllServers();
        }));
    }

    public Console getConsole() {
        return this.console;
    }

    public ServerActionRunner getActionRunner() {
        return this.actionRunner;
    }

    public TemplateManager getTemplateManager() {
        return templateManager;
    }
}
