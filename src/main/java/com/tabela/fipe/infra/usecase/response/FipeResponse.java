package com.tabela.fipe.infra.usecase.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FipeResponse {

    @JsonProperty("Valor")
    private String valor;

    @JsonProperty("Marca")
    private String marca;

    @JsonProperty("Modelo")
    private String modelo;

    @JsonProperty("AnoModelo")
    private String anoModelo;

    @JsonProperty("Combustivel")
    private String combustivel;

    @JsonIgnore
    @JsonProperty("CodigoFipe")
    private String codigoFipe;

    @JsonProperty("MesReferencia")
    private String mesReferencia;

    @JsonIgnore
    @JsonProperty("Autenticacao")
    private String autenticacao;

    @JsonIgnore
    @JsonProperty("TipoVeiculo")
    private String tipoVeiculo;

    @JsonIgnore
    @JsonProperty("SiglaCombustivel")
    private String siglaCombustivel;

    @JsonProperty("DataConsulta")
    private String dataConsulta;
}
