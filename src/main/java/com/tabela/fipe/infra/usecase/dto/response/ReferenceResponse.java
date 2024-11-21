package com.tabela.fipe.infra.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReferenceResponse {

    @JsonProperty("Codigo")
    private Integer codigo;

    @JsonProperty("Mes")
    private String mes;

    public String getMonth() {
        return this.getMes().split("/")[0].trim();
    }

    public Integer getYear() {
        return Integer.parseInt(this.getMes().split("/")[1].trim());
    }
}
