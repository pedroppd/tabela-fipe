package com.tabela.fipe.infra.shared;

import com.tabela.fipe.infra.usecase.dto.request.FipeTable;
import com.tabela.fipe.infra.usecase.dto.response.FipeResponse;
import com.tabela.fipe.infra.usecase.dto.response.HistoricFipeResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.tabela.fipe.infra.shared.JSON.parse;

public class TableFipe {
    public static String[] getHeaders() {
        return new String[]{"Content-Type", "application/json", "User-Agent", "insomnia/10.0.0"};
    }

    public static List<HistoricFipeResponse> getHistoricFipeResponse(final List<CompletableFuture<Pair<FipeTable, ResponseEntity<String>>>> futureFipeTableList) {
        return futureFipeTableList
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .map((fipe) -> {
                    if (fipe.getRight().getStatusCode().is2xxSuccessful()) {
                        return new HistoricFipeResponse(fipe.getLeft(), parse(fipe.getRight().getBody(), FipeResponse.class), fipe.getRight().getStatusCode().value());
                    }
                    return new HistoricFipeResponse(fipe.getLeft(), null, fipe.getRight().getStatusCode().value());
                })
                .toList();
    }
}
