package com.nexoscript.servermanager.api;

import com.nexoscript.servermanager.api.server.IServerActionRunner;

public interface IServerManagerWorker {
    IServerActionRunner actionRunner();
}
