package com.nexoscript.servermanager.node;

import com.nexoscript.servermanager.node.console.Console;
import com.nexoscript.servermanager.node.server.ServerActionRunner;
import com.nexoscript.servermanager.node.template.TemplateManager;
import com.nexoscript.servermanger.api.IServerManagerNode;
import com.nexoscript.servermanger.api.console.IConsole;
import com.nexoscript.servermanger.api.server.IServerActionRunner;
import com.nexoscript.servermanger.api.template.ITemplateManager;

public class ServerManagerNode implements IServerManagerNode {
    private final ITemplateManager templateManager;
    private final IServerActionRunner actionRunner;

    private final IConsole console;

    public ServerManagerNode() {
        Provider.register(this);
        this.templateManager = new TemplateManager();
        this.actionRunner = new ServerActionRunner(this, this.templateManager);
        this.console = new Console(this.templateManager, this.actionRunner);
        this.console.start();
    }

    @Override
    public void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nProgramm has been exited. Stop all servers...");
            this.actionRunner.shutdownAllServers();
        }));
    }

    @Override
    public IConsole console() {
        return this.console;
    }

    @Override
    public IServerActionRunner actionRunner() {
        return this.actionRunner;
    }

    @Override
    public ITemplateManager templateManager() {
        return templateManager;
    }

    public static void main(String[] args) {
        ServerManagerNode node = new ServerManagerNode();
        node.shutdownHook();
    }
}
