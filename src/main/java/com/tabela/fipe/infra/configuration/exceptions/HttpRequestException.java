package com.tabela.fipe.infra.configuration.exceptions;

public class HttpRequestException extends RuntimeException {

    private String message;

    public HttpRequestException(final String message) {
        this.message = message;
    }
}
