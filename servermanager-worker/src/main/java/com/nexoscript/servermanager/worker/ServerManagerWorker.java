package com.nexoscript.servermanager.worker;

import com.nexoscript.servermanager.worker.server.ServerActionRunner;
import com.nexoscript.servermanager.api.IServerManagerWorker;
import com.nexoscript.servermanager.api.server.IServerActionRunner;
import com.nexoscript.servermanager.api.template.ITemplateManager;

public record ServerManagerWorker(IServerActionRunner actionRunner) implements IServerManagerWorker {

    public ServerManagerWorker(ITemplateManager actionRunner) {
        this(new ServerActionRunner(actionRunner));
    }

    public static void main(String[] args) {
        /*
         * TODO
         * Start networking
         * Get TemplateManager from node and automatically sync changes
         * */
    }
}
