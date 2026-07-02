package com.hospedagem.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders
        .standaloneSetup(new ControllerTeste())
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();

    @ParameterizedTest
    @ValueSource(strings = {
        "data", "capacidade", "recurso", "alteracao", "tipo", "valor",
        "numero-camas", "adicional", "tipo-cama", "lista-camas", "ambientes",
        "numero-hospedes", "aluguel-cancelado"
    })
    void excecoesDeRequisicao_deveMapearPara400(String tipo) throws Exception {
        mockMvc.perform(get("/teste/{tipo}", tipo))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").isNotEmpty());
    }

    @Test
    void globalHandler_deveMapearQuartoIndisponivelPara409() throws Exception {
        mockMvc.perform(get("/teste/indisponivel"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.erro").isNotEmpty());
    }

    @Test
    void entityNotFound_deveMapearPara404() throws Exception {
        mockMvc.perform(get("/teste/nao-encontrado"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.erro").isNotEmpty());
    }

    @RestController
    @RequestMapping("/teste")
    static class ControllerTeste {

        @GetMapping("/{tipo}")
        void lancar(@PathVariable("tipo") String tipo) {
            throw switch (tipo) {
                case "data" -> new DataInvalidaException("Data inválida.");
                case "capacidade" -> new CapacidadeExcedidaException(2, 3);
                case "recurso" -> new RecursoNaoPermitidoException("berço", "Quarto Individual");
                case "alteracao" -> new AlteracaoTipoQuartoNaoPermitidaException();
                case "tipo" -> TipoQuartoInvalidoException.valorInvalido();
                case "valor" -> new ValorBaseInvalidoException();
                case "numero-camas" -> new NumeroCamasInvalidoException();
                case "adicional" -> new AdicionalCamaInvalidoException();
                case "tipo-cama" -> new TipoCamaObrigatorioException();
                case "lista-camas" -> new ListaCamasObrigatoriaException();
                case "ambientes" -> new QuantidadeAmbientesInvalidaException();
                case "numero-hospedes" -> new NumeroHospedesInvalidoException();
                case "aluguel-cancelado" -> new AluguelJaCanceladoException();
                case "indisponivel" -> new QuartoIndisponivelException(1L);
                case "nao-encontrado" -> new EntityNotFoundException("Não encontrado.");
                default -> new IllegalArgumentException("Tipo de teste inválido.");
            };
        }
    }
}
