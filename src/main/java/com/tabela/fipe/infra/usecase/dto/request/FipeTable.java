package com.tabela.fipe.infra.usecase.dto.request;

public record FipeTable(int codigoTipoVeiculo,
                        int codigoTabelaReferencia,
                        int codigoModelo,
                        int codigoMarca,
                        int codigoTipoCombustivel,
                        int anoModelo,
                        String tipoVeiculo,
                        String modeloCodigoExterno,
                        String tipoConsulta) {
}
