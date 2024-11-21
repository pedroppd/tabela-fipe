package com.tabela.fipe.application;

import com.tabela.fipe.infra.usecase.FindFipeTableHistoricUseCase;
import com.tabela.fipe.application.dto.request.FipeTableHistoricRequestDTO;
import com.tabela.fipe.infra.usecase.response.FipeResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class FipeTableHistoricGateway {

    private final FindFipeTableHistoricUseCase buscarHistoricoFipe;

    @PostMapping("/tabela-fipe-historico")
    public ResponseEntity<List<FipeResponse>> buscar(@RequestBody final FipeTableHistoricRequestDTO fipeTableHistorico,
                                                     @RequestParam(name = "mes", required = false) final String month,
                                                     @RequestParam(name = "anoInicio", required = false) Integer beginYear,
                                                     @RequestParam(name = "anoFim", required = false) final Integer endYear) {
        final var fipeResponse = buscarHistoricoFipe.execute(fipeTableHistorico, month, beginYear, endYear);
        return ResponseEntity.ok().body(fipeResponse);
    }
}
