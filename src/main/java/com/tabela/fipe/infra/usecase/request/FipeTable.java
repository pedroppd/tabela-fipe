package com.tabela.fipe.infra.usecase.request;

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
