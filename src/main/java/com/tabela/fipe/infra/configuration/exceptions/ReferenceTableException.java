package com.tabela.fipe.infra.configuration.exceptions;

import lombok.Getter;

@Getter
public class ReferenceTableException extends RuntimeException {

    private String message;
    private Integer statusCode;

    public ReferenceTableException(final String message, final Integer statusCode) {
        super();
        this.statusCode = statusCode;
        this.message = message;
    }
}
