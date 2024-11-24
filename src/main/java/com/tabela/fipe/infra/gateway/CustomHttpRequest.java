package com.tabela.fipe.infra.gateway;


import com.tabela.fipe.infra.configuration.exceptions.HttpRequestException;
import com.tabela.fipe.infra.usecase.FindFipeTableHistoricUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tabela.fipe.infra.shared.JSON.stringify;

@Service
public class CustomHttpRequest {

    private final HttpClient client;

    private static final Logger logger = LoggerFactory.getLogger(CustomHttpRequest.class);

    public CustomHttpRequest() {
        this.client = HttpClient.newHttpClient();
    }

    public <R> ResponseEntity<String> post(final String url,
                                           final String[] headers,
                                           final R body) {
        try {
            final String jsonBody = stringify(body);
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            logger.error("Error to try catch the {} - {} - {}", url, ex.getMessage(), Thread.currentThread().getName());
            throw new HttpRequestException(ex.getMessage());
        }
    }

    public <R> ResponseEntity<String> postWithRetry(final String url,
                                                    final String[] headers,
                                                    final R body,
                                                    final Duration duration,
                                                    AtomicInteger counter) {
        try {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.ofString(stringify(body)))
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && counter.get() > 0) {
                logger.info("Retentativa número {} para a thread {} - StatusCode: {} - url: {}", counter.get(), Thread.currentThread().getName(), response.statusCode(), url);
                counter.decrementAndGet();
                Thread.sleep(duration);
                this.postWithRetry(url, headers, body, duration, counter);
            }
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            logger.error("Error to try catch the {} - {} - {}", url, ex.getMessage(), Thread.currentThread().getName());
            throw new HttpRequestException(ex.getMessage());
        }
    }

    public <R> ResponseEntity<String> postWithRetry(final String url,
                                                    final String[] headers,
                                                    final Duration duration,
                                                    AtomicInteger counter) {
        try {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && counter.get() > 0) {
                logger.info("Retentativa número {} para a thread {} - StatusCode: {} - Url: {}", counter.get(), Thread.currentThread().getName(), response.statusCode(), url);
                counter.decrementAndGet();
                Thread.sleep(duration);
                this.postWithRetry(url, headers, duration, counter);
            }
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            logger.error("Error to make api request - {} - {} - {}", url, ex.getMessage(), Thread.currentThread().getName());
            throw new HttpRequestException(ex.getMessage());
        }
    }
}