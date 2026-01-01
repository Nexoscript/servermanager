package com.nexoscript.servermanager.node;

import com.nexoscript.servermanager.node.console.Console;
import com.nexoscript.servermanager.node.server.ServerActionRunner;
import com.nexoscript.servermanager.node.template.TemplateManager;
import com.nexoscript.servermanger.api.IServerManager;

public class ServerManagerNode implements IServerManager {
    private final TemplateManager templateManager;
    private final ServerActionRunner actionRunner;

    private final Console console;

    public ServerManagerNode() {
        this.templateManager = new TemplateManager();
        this.actionRunner = new ServerActionRunner(this, this.templateManager);
        this.console = new Console(this.templateManager, this.actionRunner);
        this.console.start();
    }

    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm has been exited. Stop all servers...");
            this.actionRunner.shutdownAllServers();
        }));
    }

    public Console console() {
        return this.console;
    }

    public ServerActionRunner actionRunner() {
        return this.actionRunner;
    }

    public TemplateManager templateManager() {
        return templateManager;
    }

    public static void main(String[] args) {
        ServerManagerNode node = new ServerManagerNode();
        node.shutdownHook();
    }
}
