package com.tabela.fipe.infra.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.usecase.dto.request.FipeTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class HistoricFipeResponse {

    @JsonProperty("request")
    private FipeTable fipeTable;

    @JsonProperty("response")
    private FipeResponse fipeResponse;

    @JsonProperty("statusCode")
    private Integer statusCode;
}
