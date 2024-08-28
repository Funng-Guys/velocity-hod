package com.philippgitpush;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
  id = "hod",
  name = "Hod",
  version = "0.0.1-SNAPSHOT",
  url = "https://github.com/philippgitpush",
  description = "Service for Hetzner-On-Demand",
  authors = {"philippgitpush", "olofthesnowman"}
)
public class Main {

  private final ProxyServer server;
  private final Logger logger;
  private final ConfigManager configManager;
  private final HttpClientService httpClientService;
  private final PlayerCountTask playerCountTask;

  @Inject
  public Main(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
    this.server = server;
    this.logger = logger;
    this.configManager = new ConfigManager(dataDirectory);
    this.httpClientService = new HttpClientService();
    this.playerCountTask = new PlayerCountTask(httpClientService, configManager, server, logger);

    playerCountTask.startPeriodicPlayerCountTask();

    logger.info("Hetzner-On-Demand: Plugin initialized. Ready for requests.");
  }

  @Subscribe(order = PostOrder.NORMAL)
  public void onPostLoginEvent(PostLoginEvent event) {
    event.getPlayer().sendActionBar(Component.text("Connecting, please wait ..."));
    playerCountTask.sendPlayerCountAsync();
  }

  @Subscribe
  public void onProxyShutdown(ProxyShutdownEvent event) {
    playerCountTask.stopScheduler();
  }
}
