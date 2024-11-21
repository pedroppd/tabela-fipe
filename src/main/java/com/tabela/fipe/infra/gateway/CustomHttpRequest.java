package com.tabela.fipe.infra.gateway;

import com.tabela.fipe.infra.shared.JSON;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tabela.fipe.infra.shared.JSON.stringify;

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
            final String jsonBody = stringify(body);
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            System.out.println("Erro na chamada thread: " + Thread.currentThread().getName());
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
            throw new RuntimeException(ex);
        }
    }

    public <R> ResponseEntity<String> postWithRetry(final String url,
                                                    final String[] headers,
                                                    final R body,
                                                    AtomicInteger counter) {
        try {
            final HttpRequest request = HttpRequest.newBuilder().uri(new URI(url))
                    .headers(headers)
                    .POST(HttpRequest.BodyPublishers.ofString(stringify(body)))
                    .build();
            final var response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && counter.get() > 0) {
                System.out.println("Retentativa número " + counter.get() + " para a thread " + Thread.currentThread().getName() + " StatusCode " + response.statusCode());
                counter.decrementAndGet();
                Thread.sleep(1000L);
                this.postWithRetry(url, headers, body, counter);
            }
            System.out.println("Chamada feita com sucesso " + Thread.currentThread().getName());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception ex) {
            System.out.println("Erro na chamada thread: " + Thread.currentThread().getName());
            ex.printStackTrace();
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