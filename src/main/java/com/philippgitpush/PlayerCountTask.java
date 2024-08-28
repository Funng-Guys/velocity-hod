package com.philippgitpush;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerCountTask {

  private final HttpClientService httpClientService;
  private final ConfigManager configManager;
  private final ProxyServer server;
  private final Logger logger;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public PlayerCountTask(HttpClientService httpClientService, ConfigManager configManager, ProxyServer server, Logger logger) {
    this.httpClientService = httpClientService;
    this.configManager = configManager;
    this.server = server;
    this.logger = logger;
  }

  public void sendPlayerCountAsync() {
    int playerCount = server.getPlayerCount();
    JsonObject jsonPayload = new JsonObject();
    jsonPayload.addProperty("playerCount", playerCount);
    String url = configManager.getRequestUri();
    httpClientService.sendJsonRequestAsync(url, jsonPayload, logger);
  }

  public void startPeriodicPlayerCountTask() {
    Runnable task = this::sendPlayerCountAsync;
    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
  }

  public void stopScheduler() {
    if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdown();
  }
}
