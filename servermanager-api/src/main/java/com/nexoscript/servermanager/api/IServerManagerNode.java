package com.nexoscript.servermanager.api;

import com.nexoscript.servermanager.api.console.IConsole;
import com.nexoscript.servermanager.api.template.ITemplateManager;

public interface IServerManagerNode {
    void shutdownHook();
    IConsole console();
    IServerManagerWorker interalWorker();
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