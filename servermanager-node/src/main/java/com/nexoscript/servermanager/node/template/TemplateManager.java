package com.nexoscript.servermanager.node.template;

import com.nexoscript.servermanager.node.config.JsonConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TemplateManager {
    private final String templatesPath;
    private final JsonConfig templatesConfig;
    private final Map<String, Path> templates;

    public TemplateManager() {
        this.templatesPath = "templates/";
        File templatesDirectory = new File(templatesPath);
        if (!templatesDirectory.exists()) {
            if (!templatesDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create directory " + templatesPath);
            }
        }
        this.templatesConfig = new JsonConfig("templates.json");
        this.templatesConfig.addDefault("templates", new JSONArray());
        this.templates = new HashMap<>();
        JSONArray templatesArray = (JSONArray) this.templatesConfig.get("templates");
        for (int i = 0; i < templatesArray.length(); i++) {
            JSONObject templateObject = (JSONObject) templatesArray.get(i);
            this.templates.put(templateObject.getString("name"), Path.of(templateObject.getString("path")));
        }
    }

    public void createTemplate(String name) {
        if (this.getTemplate(name) != null) {
            System.out.println("Template " + name + " already exists");
            return;
        }
        File templateDirectory = new File(templatesPath, name);
        if (!templateDirectory.exists()) {
            if (!templateDirectory.mkdirs()) {
                System.out.println("Failed to create directory " + templatesPath);
                return;
            }
            if (!templateDirectory.setWritable(true, false)) {
                System.out.println("Failed to set directory " + templatesPath + " writable");
                return;
            }
        }
        JSONArray templatesArray = (JSONArray) this.templatesConfig.get("templates");
        JSONObject templateObject = new JSONObject();
        templateObject.put("name", name);
        templateObject.put("path", templateDirectory.getPath());
        templatesArray.put(templateObject);
        if (this.templatesConfig.set("templates", templatesArray)) {
            this.templates.put(name, templateDirectory.toPath());
            return;
        }
        System.out.println("Error creating template " + name);
    }

    public void renameTemplate(String name, String newName) {
        if (this.getTemplate(name) == null) {
            System.out.println("Template " + name + " does not exist");
            return;
        }
        try {
            this.createTemplate(newName);
            System.out.println(this.getTemplate(name).toString());
            System.out.println(this.getTemplate(newName).toString());
            Files.move(this.getTemplate(name), this.getTemplate(newName), StandardCopyOption.ATOMIC_MOVE);
            JSONArray templatesArray = (JSONArray) this.templatesConfig.get("templates");
            for (int i = 0; i < templatesArray.length(); i++) {
                JSONObject object = templatesArray.getJSONObject(i);
                if (object.getString("name").equals(name)) {
                    templatesArray.remove(i);
                    break;
                }
            }
            if (this.templatesConfig.set("templates", templatesArray)) {
                this.templates.remove(name);
                return;
            }
            System.out.println("Error deleting template " + name);
        } catch (Exception e) {
            this.deleteTemplate(newName);
            System.out.println("Error renaming template " + name);
            e.printStackTrace();
        }
    }

    public void deleteTemplate(String name) {
        try {
            if (this.getTemplate(name) == null) {
                System.out.println("Template " + name + " does not exist");
                return;
            }
            Files.walkFileTree(this.getTemplate(name), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            JSONArray templatesArray = (JSONArray) this.templatesConfig.get("templates");
            for (int i = 0; i < templatesArray.length(); i++) {
                JSONObject object = templatesArray.getJSONObject(i);
                if (object.getString("name").equals(name)) {
                    templatesArray.remove(i);
                    break;
                }
            }
            if (this.templatesConfig.set("templates", templatesArray)) {
                this.templates.remove(name);
                return;
            }
            System.out.println("Error deleting template " + name);
        } catch (Exception e) {
            System.out.println("Error deleting template " + name);
            e.printStackTrace();
        }
    }

    public Path getTemplate(String name) {
        JSONArray templatesArray = (JSONArray) this.templatesConfig.get("templates");
        for (int i = 0; i < templatesArray.length(); i++) {
            JSONObject object = templatesArray.getJSONObject(i);
            if (object.getString("name").equals(name)) {
                return Path.of(object.getString("path"));
            }
        }
        return null;
    }
}
