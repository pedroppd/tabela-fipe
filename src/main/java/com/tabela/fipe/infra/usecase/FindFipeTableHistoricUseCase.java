package com.tabela.fipe.infra.usecase;

import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.gateway.CustomHttpRequest;
import com.tabela.fipe.infra.usecase.request.FipeTable;
import com.tabela.fipe.infra.usecase.response.FipeResponse;
import com.tabela.fipe.infra.usecase.response.ReferenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tabela.fipe.infra.shared.JSON.*;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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

    public List<FipeResponse> execute(final FipeTableHistoricRequestDTO historicFipeTable,
                                      String month,
                                      Integer beginYear,
                                      Integer endYear) {
        final ResponseEntity<String> referenceTableFuture = httpRequest.post(urlReferencia, getHeaders());
        final List<ReferenceResponse> referenceList = parseReferenceResponseList(referenceTableFuture.getBody());

        final List<FipeTable> fipeTableRequestList = referenceList
                .stream()
                .filter(rf -> rf.getYear().compareTo(historicFipeTable.anoModelo()) >= 0
                        && filterByMonth(rf, month)
                        && filterByYears(rf, beginYear, endYear))
                .map(rf -> createFipeTable(historicFipeTable, rf.getCodigo()))
                .toList();

        final var futureFipeTableList = fipeTableRequestList
                .stream()
                .map(ft -> supplyAsync(() -> httpRequest.postWithRetry(urlConsultarFipe, getHeaders(), ft, new AtomicInteger(5))))
                .toList();

        return futureFipeTableList
                .stream()
                .map(CompletableFuture::join)
                .filter(fipe -> fipe.getStatusCode().value() == 200)
                .map(fipe -> parse(fipe.getBody(), FipeResponse.class))
                .toList();
    }

    private boolean filterByMonth(ReferenceResponse referenceResponse, String month) {
        if (!isEmpty(month))
            return referenceResponse.getMonth().compareToIgnoreCase(month) == 0;
        return TRUE;
    }

    private boolean filterByYears(ReferenceResponse referenceResponse, Integer beginyear, Integer endYear) {
        if (nonNull(beginyear) && nonNull(endYear))
            return referenceResponse.getYear().compareTo(beginyear) >= 0 && referenceResponse.getYear().compareTo(endYear) <= 0;
        return TRUE;
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
