package com.nexoscript.servermanager.node.server;

import com.nexoscript.servermanager.node.ServerManagerNode;
import com.nexoscript.servermanger.api.IServerManagerNode;
import com.nexoscript.servermanger.api.server.IServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Server implements IServer {
    private final IServerManagerNode node;
    private final String name;
    private final String path;
    private final String jar;
    private Process process;
    private Thread outputThread;
    private volatile boolean insideConsole = false;
    private final List<String> logBuffer = Collections.synchronizedList(new ArrayList<>());
    private BufferedWriter writer;

    public Server(IServerManagerNode node, String name, String path, String jar) {
        this.node = node;
        this.name = name;
        this.path = path;
        this.jar = jar;
    }

    @Override
    public void start() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-Xmx4G", "-Xms1G", "-Dcom.mojang.eula.agree=true", "-DIReallyKnowWhatIAmDoingISwear",
                    "-jar", jar, "nogui").directory(new File(path)).redirectErrorStream(true);
            process = processBuilder.start();
            outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        synchronized (logBuffer) {
                            logBuffer.add(line);
                        }
                        if (insideConsole) {
                            System.out.println("[" + name + "] " + line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    this.node.actionRunner().servers().remove(name);
                    insideConsole = false;
                }
            });
            outputThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if (process != null && process.isAlive()) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write("stop");
                writer.newLine();
                writer.flush();
            } catch (IOException ignored) {
            }
            System.out.println("Server '" + name + "' wurde gestoppt");
            process.destroy();
        }
        try {
            if (outputThread != null) {
                outputThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void console(Scanner scanner) {
        if (process == null || !process.isAlive()) {
            System.out.println("Server '" + name + "' läuft nicht.");
            return;
        }
        insideConsole = true;
        if (process != null && process.isAlive()) {
            try {
                writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                synchronized (logBuffer) {
                    for (String log : logBuffer) {
                        System.out.println("[" + name + "] " + log);
                    }
                }
                System.out
                        .println("Konsole von Server '" + name + "' geöffnet. Tippen Sie 'leave', um zurückzukehren.");
                while (insideConsole) {
                    String command = scanner.nextLine();
                    if (command.equalsIgnoreCase("leave")) {
                        System.out.println("Konsole von Server '" + name + "' geschlossen.");
                        insideConsole = false;
                        break;
                    }
                    writer.write(command);
                    writer.newLine();
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                insideConsole = false;
            }
            return;
        }
        insideConsole = false;
    }
}
