package com.tabela.fipe.application.dto.request;

public record FipeTableHistoricRequestDTO(Integer codigoTipoVeiculo,
                                          Integer codigoModelo,
                                          Integer codigoMarca,
                                          Integer anoModelo) { }
