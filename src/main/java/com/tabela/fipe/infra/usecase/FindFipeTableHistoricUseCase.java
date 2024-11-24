package com.tabela.fipe.infra.usecase;

import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.configuration.exceptions.ReferenceTableException;
import com.tabela.fipe.infra.gateway.CustomHttpRequest;
import com.tabela.fipe.infra.usecase.dto.request.FipeTable;
import com.tabela.fipe.infra.usecase.dto.response.FipeResponse;
import com.tabela.fipe.infra.usecase.dto.response.ReferenceResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tabela.fipe.infra.shared.JSON.*;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.lang.Boolean.TRUE;
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

    private static final Logger logger = LoggerFactory.getLogger(FindFipeTableHistoricUseCase.class);

    public List<FipeResponse> execute(final FipeTableHistoricRequestDTO historicFipeTable,
                                      final List<String> months,
                                      final Integer beginYear,
                                      final Integer endYear) {
        logger.info("Getting reference tables - {}", Thread.currentThread().getName());
        final ResponseEntity<String> referenceTableFuture = httpRequest.postWithRetry(urlReferencia, getHeaders(), Duration.ofSeconds(5), new AtomicInteger(3));
        if (referenceTableFuture.getStatusCode().value() != 200) {
            logger.error("Error to try catch the references: {} - {}", referenceTableFuture.getStatusCode(), Thread.currentThread().getName());
            throw new ReferenceTableException("Error to try catch the references", referenceTableFuture.getStatusCode().value());
        }
        final List<ReferenceResponse> referenceList = parseReferenceResponseList(referenceTableFuture.getBody());

        logger.info("Filtering reference tables - {}", Thread.currentThread().getName());
        final List<FipeTable> fipeTableRequestList = referenceList
                .stream()
                .filter(rf -> rf.getYear().compareTo(historicFipeTable.anoModelo()) >= 0
                        && filterByMonth(rf, months)
                        && filterByYears(rf, beginYear, endYear))
                .map(rf -> createFipeTable(historicFipeTable, rf.getCodigo()))
                .toList();

        logger.info("Doing requests - {}", Thread.currentThread().getName());
        final var futureFipeTableList = fipeTableRequestList
                .stream()
                .map(ft -> supplyAsync(() -> httpRequest.postWithRetry(urlConsultarFipe, getHeaders(), ft, Duration.ofSeconds(2), new AtomicInteger(5)))
                        .handle((result, ex) -> {
                            if (nonNull(ex)) {
                                logger.error("Error processing request", ex);
                                return null;
                            }
                            return result;
                        }))
                .toList();

        CompletableFuture.allOf(futureFipeTableList.toArray(new CompletableFuture[0])).join();

        return futureFipeTableList
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .filter(fipe -> fipe.getStatusCode().value() == 200)
                .map(fipe -> parse(fipe.getBody(), FipeResponse.class))
                .toList();
    }

    private boolean filterByMonth(ReferenceResponse referenceResponse, List<String> months) {
        if (!months.isEmpty()) {
            final var monthsLowerCase = months.stream().map(String::toLowerCase).toList();
            return monthsLowerCase.contains(referenceResponse.getMonth().toLowerCase());
        }
        return TRUE;
    }

    private boolean filterByYears(ReferenceResponse referenceResponse, Integer beginYear, Integer endYear) {
        if (nonNull(beginYear) && nonNull(endYear))
            return referenceResponse.getYear().compareTo(beginYear) >= 0 && referenceResponse.getYear().compareTo(endYear) <= 0;
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
