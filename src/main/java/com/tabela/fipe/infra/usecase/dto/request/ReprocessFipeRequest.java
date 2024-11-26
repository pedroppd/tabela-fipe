package com.tabela.fipe.infra.usecase.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tabela.fipe.infra.usecase.dto.response.FipeResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReprocessFipeRequest {

    @JsonProperty("request")
    private FipeTable fipeTable;

    @JsonProperty("response")
    private FipeResponse fipeResponse;

    @JsonProperty("statusCode")
    private Integer statusCode;
}
