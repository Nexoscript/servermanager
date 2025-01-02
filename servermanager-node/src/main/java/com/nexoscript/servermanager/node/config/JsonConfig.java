package com.nexoscript.servermanager.node.config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfig {
    private Path configFile;
    private JSONObject jsonObject;

    public JsonConfig(String configName) {
        this.create(".", configName);
    }

    public JsonConfig(String path, String configName) {
        this.create(path, configName);
    }

    private void create(String path, String configName) {
        this.configFile = Path.of(path, configName);
        File file = new File(this.configFile.toString());
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    this.jsonObject = new JSONObject();
                    this.save();
                    return;
                }
                throw new IOException("File can't created. " + file.getAbsolutePath());
            }
            this.jsonObject = new JSONObject(new String(Files.readAllBytes(this.configFile)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDefault(String key, Object value) {
        if (this.get(key) == null) {
            this.jsonObject.put(key, value);
            this.save();
        }
    }

    public boolean set(String key, Object value) {
        try {
            this.jsonObject.put(key, value);
            this.save();
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    public boolean remove(String key) {
        try {
            this.jsonObject.remove(key);
            this.save();
            return true;
        } catch (JSONException ignored) {
            return false;
        }
    }

    public Object get(String key) {
        try {
            return this.jsonObject.get(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public void save() {
        File file = new File(this.configFile.toString());
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(this.jsonObject.toString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getConfigFile() {
        return configFile;
    }
}
