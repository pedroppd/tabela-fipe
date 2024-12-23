package com.tabela.fipe.infra.usecase;

import com.tabela.fipe.infra.gateway.CustomHttpRequest;
import com.tabela.fipe.infra.usecase.dto.request.ReprocessFipeRequest;
import com.tabela.fipe.infra.usecase.dto.response.FipeResponse;
import com.tabela.fipe.infra.usecase.dto.response.HistoricFipeResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tabela.fipe.infra.shared.JSON.parse;
import static com.tabela.fipe.infra.shared.TableFipe.getHeaders;
import static com.tabela.fipe.infra.shared.TableFipe.getHistoricFipeResponse;
import static java.util.Objects.nonNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;


@RequiredArgsConstructor
@Service
public class ReprocessFipeTableHistoricUseCase {

    private final CustomHttpRequest httpRequest;

    @Value("${tabelafipe.consultar.fipe}")
    private String urlConsultarFipe;

    private static final Logger logger = LoggerFactory.getLogger(ReprocessFipeTableHistoricUseCase.class);

    public List<HistoricFipeResponse> execute(final List<ReprocessFipeRequest> reprocessFipeRequest) {
        logger.info("Reprocessing fipe requests - {}", Thread.currentThread().getName());

        final var futureFipeTableList = reprocessFipeRequest
                .stream()
                .filter(reprocess -> reprocess.getStatusCode() != 200)
                .map(ft -> supplyAsync(() -> httpRequest.postWithRetry(urlConsultarFipe, getHeaders(), ft.getFipeTable(), Duration.ofMillis(1), new AtomicInteger(2)))
                        .handle((result, ex) -> {
                            if (nonNull(ex)) {
                                logger.error("Error processing request", ex);
                                return null;
                            }
                            return result;
                        }))
                .toList();

        CompletableFuture.allOf(futureFipeTableList.toArray(new CompletableFuture[0])).join();

        return getHistoricFipeResponse(futureFipeTableList);
    }
}
