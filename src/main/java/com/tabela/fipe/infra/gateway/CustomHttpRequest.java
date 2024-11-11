package com.tabela.fipe.infra.gateway;

import com.tabela.fipe.infra.shared.JSON;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CustomHttpRequest {

    private final HttpClient client;

    public CustomHttpRequest() {
        this.client = HttpClient.newHttpClient();
    }

    public <R> ResponseEntity<String> post(final String url,
                                           final String[] headers,
                                           final R body) {
        try {
            System.out.println("Fazendo chamada thread: " + Thread.currentThread().getName());
            final String jsonBody = JSON.stringify(body);
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            System.out.println("Erro na chamada thread: " + Thread.currentThread().getName());
            throw new RuntimeException(ex);
        }
    }

    public <R> ResponseEntity<String> post(final String url, final String[] headers) {
        try {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}