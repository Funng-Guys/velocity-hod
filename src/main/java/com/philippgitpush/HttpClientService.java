package com.philippgitpush;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HttpClientService {

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final Gson gson = new Gson();

  public void sendJsonRequestAsync(String url, JsonObject jsonPayload, Logger logger) {
    String requestBody = gson.toJson(jsonPayload);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    CompletableFuture<HttpResponse<String>> asyncRequest = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

    asyncRequest.thenAccept(response -> {
      if (response.statusCode() != 200) {
        logger.error("Unexpected HTTP Response: " + response.statusCode() + " - " + response.body());
        return;
      }
      logger.info("HTTP Response: " + response.statusCode());
    }).exceptionally(ex -> {
      logger.error("Failed to send HTTP request", ex);
      return null;
    });
  }
}
