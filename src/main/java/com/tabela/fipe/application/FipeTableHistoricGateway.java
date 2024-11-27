package com.tabela.fipe.application;

import com.tabela.fipe.infra.usecase.FindFipeTableHistoricUseCase;
import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.usecase.ReprocessFipeTableHistoricUseCase;
import com.tabela.fipe.infra.usecase.dto.request.ReprocessFipeRequest;
import com.tabela.fipe.infra.usecase.dto.response.FipeResponse;
import com.tabela.fipe.infra.usecase.dto.response.HistoricFipeResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tabela-fipe")
@AllArgsConstructor
public class FipeTableHistoricGateway {

    private final FindFipeTableHistoricUseCase findFipeHistoric;

    private final ReprocessFipeTableHistoricUseCase reprocessFipeTableHistoric;

    @PostMapping("/historico")
    public ResponseEntity<List<HistoricFipeResponse>> find(@RequestBody final FipeTableHistoricRequestDTO fipeTableHistorico,
                                                             @RequestParam(name = "mes", required = false) final List<String> months,
                                                             @RequestParam(name = "anoInicio", required = false) final Integer beginYear,
                                                             @RequestParam(name = "anoFim", required = false) final Integer endYear) {
        final var fipeResponse = findFipeHistoric.execute(fipeTableHistorico, months, beginYear, endYear);
        return ResponseEntity.ok().body(fipeResponse);
    }

    @PostMapping("/reprocessar")
    public ResponseEntity<List<HistoricFipeResponse>> reprocess(@RequestBody final List<ReprocessFipeRequest> reprocessFipeRequest) {
        final var fipeResponse = reprocessFipeTableHistoric.execute(reprocessFipeRequest);
        return ResponseEntity.ok().body(fipeResponse);
    }
}
