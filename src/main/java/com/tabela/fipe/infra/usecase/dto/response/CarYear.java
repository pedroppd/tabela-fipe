package com.tabela.fipe.infra.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarYear {
    @JsonProperty("Label")
    private String label;

    @JsonProperty("Value")
    private String value;


    public int getYear() {
        return Integer.parseInt(this.getValue().split("-")[0]);
    }
}
