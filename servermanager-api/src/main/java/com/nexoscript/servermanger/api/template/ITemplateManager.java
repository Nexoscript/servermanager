package com.nexoscript.servermanger.api.template;

import java.nio.file.Path;
import java.util.Map;

public interface ITemplateManager {
    void createTemplate(String name);
    void renameTemplate(String name, String newName);
    void deleteTemplate(String name);
    Path templatePath(String name);
    Map<String, Path> templates();
}
