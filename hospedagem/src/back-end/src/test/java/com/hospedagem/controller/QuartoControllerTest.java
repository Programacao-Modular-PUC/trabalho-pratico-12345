package com.hospedagem.controller;

import com.hospedagem.exception.GlobalExceptionHandler;
import com.hospedagem.exception.TipoQuartoInvalidoException;
import com.hospedagem.service.QuartoService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QuartoControllerTest {

    @Test
    void listarQuartosComTipoInvalido_deveRetornar400() throws Exception {
        QuartoService service = mock(QuartoService.class);
        when(service.listarPorTipo("TRIPLO"))
            .thenThrow(TipoQuartoInvalidoException.valorInvalido());
        MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new QuartoController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        mockMvc.perform(get("/quartos").param("tipo", "TRIPLO"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.erro").isNotEmpty());
    }
}
