package com.tabela.fipe.infra.configuration.exceptions;

import org.apache.tomcat.util.http.parser.HttpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ExceptionManager {

    private final Logger logger = LoggerFactory.getLogger(ExceptionManager.class);
    private static final String ERROR_MESSAGE = "Ocorreu um erro ao tentar fazer a requisição. Tente novamente mais tarde.";


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericException(final Exception ex) {
        logger.error("Unexpected error occurred", ex);
        final ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(List.of(ERROR_MESSAGE))
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<ApiError> httpRequestException(final HttpRequestException ex) {
        logger.error("Erro to try make http request", ex);
        final ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .errors(List.of(ERROR_MESSAGE))
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ReferenceTableException.class)
    public ResponseEntity<ApiError> referenceTableException(final ReferenceTableException ex) {
        logger.error("Erro to try catch the reference table", ex);
        final HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode());
        final ApiError apiError = ApiError
                .builder()
                .timestamp(LocalDateTime.now())
                .code(httpStatus.value())
                .status(httpStatus.name())
                .errors(List.of(ERROR_MESSAGE))
                .build();
        return new ResponseEntity<>(apiError, httpStatus);
    }
}
