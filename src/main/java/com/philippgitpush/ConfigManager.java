package com.philippgitpush;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ConfigManager {

  private final Properties properties = new Properties();

  public ConfigManager(Path dataDirectory) {
    loadConfig(dataDirectory.resolve("plugin.conf"));
  }

  private void loadConfig(Path configPath) {
    try {
      if (!Files.exists(configPath.getParent())) Files.createDirectories(configPath.getParent());
      if (!Files.exists(configPath)) createDefaultConfig(configPath);
      try (InputStream input = Files.newInputStream(configPath)) {
        properties.load(input);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void createDefaultConfig(Path configPath) {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("plugin.conf")) {
      if (input == null) throw new IOException("Default configuration file not found in JAR");
      Files.copy(input, configPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getRequestUri() {
    return properties.getProperty("request.uri", "https://example.com/");
  }
}
