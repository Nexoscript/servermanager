package com.nexoscript.servermanager.node;

import com.nexoscript.servermanager.node.console.Console;
import com.nexoscript.servermanager.node.template.TemplateManager;
import com.nexoscript.servermanager.worker.ServerManagerWorker;
import com.nexoscript.servermanager.api.IServerManagerNode;
import com.nexoscript.servermanager.api.IServerManagerWorker;
import com.nexoscript.servermanager.api.console.IConsole;
import com.nexoscript.servermanager.api.template.ITemplateManager;

public class ServerManagerNode implements IServerManagerNode {
    private final ITemplateManager templateManager;
    private final IServerManagerWorker internalWorker;

    private final IConsole console;

    public ServerManagerNode() {
        IServerManagerNode.Provider.register(this);
        this.templateManager = new TemplateManager();
        this.internalWorker = new ServerManagerWorker(this.templateManager);
        this.console = new Console(this.templateManager, this.internalWorker.actionRunner());
        this.console.start();
    }

    @Override
    public void shutdownHook() {
    }

    @Override
    public IConsole console() {
        return this.console;
    }

    @Override
    public IServerManagerWorker interalWorker() {
        return internalWorker;
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
