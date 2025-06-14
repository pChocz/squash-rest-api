package com.pj.squashrestapp.hexagonal.healthcheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthcheckService {

    private final HealthcheckConfig healthcheckConfig;

    public void ping() {
        if (StringUtils.isEmpty(healthcheckConfig.getUrl())) {
            log.error("Healthcheck URL is empty");
            return;
        }

        try {
            final URI uri = URI.create(healthcheckConfig.getUrl());
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .header("accept", "application/json")
                    .GET()
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Healthcheck response {}: {}", response.statusCode(), response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Error when sending healthcheck ping request", e);
        }
    }
}
