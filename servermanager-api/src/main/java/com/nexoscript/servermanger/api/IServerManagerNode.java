package com.nexoscript.servermanger.api;

import com.nexoscript.servermanger.api.console.IConsole;
import com.nexoscript.servermanger.api.server.IServerActionRunner;
import com.nexoscript.servermanger.api.template.ITemplateManager;

public interface IServerManagerNode {
    void shutdownHook();
    IConsole console();
    IServerActionRunner actionRunner();
    ITemplateManager templateManager();

    class Provider {
        private static IServerManagerNode instance;

        public static void register(IServerManagerNode instance) {
            if (Provider.instance != null) {
                throw new RuntimeException("Instance already registered.");
            }
            Provider.instance = instance;
        }

        public static IServerManagerNode get() {
            return Provider.instance;
        }
    }
}