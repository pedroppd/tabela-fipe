package com.tabela.fipe.infra.usecase;

import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.gateway.CustomHttpRequest;
import com.tabela.fipe.infra.usecase.request.FipeTable;
import com.tabela.fipe.infra.usecase.response.FipeResponse;
import com.tabela.fipe.infra.usecase.response.ReferenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static com.tabela.fipe.infra.shared.JSON.*;
import static java.util.concurrent.CompletableFuture.supplyAsync;


@RequiredArgsConstructor
@Service
public class FindFipeTableHistoricUseCase {
    private static final String TIPO_CONSULTA = "tradicional";
    private static final String TIPO_VEICULO = "carro";
    private static final String MODELO_CODIGO_EXTERNO = "";
    private final CustomHttpRequest httpRequest;
    @Value("${tabelafipe.consultar.fipe}")
    private String urlConsultarFipe;
    @Value("${tabelafipe.consultar.referencia}")
    private String urlReferencia;

    public List<FipeResponse> execute(final FipeTableHistoricRequestDTO historicFipeTable) {
        final var referenceTableFuture = httpRequest.post(urlReferencia, getHeaders());
        final List<ReferenceResponse> referenceList = parseReferenceResponseList(referenceTableFuture.getBody());
        final List<FipeTable> fipeTableRequest = referenceList
                .stream()
                .map(rf -> rf.getYear().compareTo(historicFipeTable.anoModelo()) >= 0 ? createFipeTable(historicFipeTable, rf.getCodigo()) : null)
                .toList();

        final var futureFipeTable = fipeTableRequest
                .stream()
                .map(ft -> supplyAsync(() -> httpRequest.post(urlConsultarFipe, getHeaders(), ft)))
                .toList();

        return futureFipeTable
                .stream()
                .map(CompletableFuture::join)
                .filter(fipe -> fipe.getStatusCode().value() == 200)
                .map(fipe -> parse(fipe.getBody(), FipeResponse.class))
                .toList();
    }

    private static String[] getHeaders() {
        return new String[]{"Content-Type", "application/json"};
    }

    private FipeTable createFipeTable(FipeTableHistoricRequestDTO fipeTable, int referenceTable) {
        return new FipeTable(fipeTable.codigoTipoVeiculo(),
                referenceTable,
                fipeTable.codigoModelo(),
                fipeTable.codigoMarca(),
                1,
                fipeTable.anoModelo(),
                TIPO_VEICULO,
                MODELO_CODIGO_EXTERNO,
                TIPO_CONSULTA);
    }
}
