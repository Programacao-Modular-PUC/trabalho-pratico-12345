package com.hospedagem.service;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraDiariasTest {

    private final CalculadoraDiarias calculadora = new CalculadoraDiarias();

    @Test
    void entradaAntesDas12_deveConsiderarDiariaIniciadaNoDiaAnterior() {
        int diarias = calculadora.calcular(
            LocalDateTime.of(2026, 7, 10, 10, 0),
            LocalDateTime.of(2026, 7, 10, 12, 0)
        );

        assertEquals(1, diarias);
    }

    @Test
    void entradaApos12_deveContarDiariaCompleta() {
        int diarias = calculadora.calcular(
            LocalDateTime.of(2026, 7, 10, 18, 30),
            LocalDateTime.of(2026, 7, 11, 12, 0)
        );

        assertEquals(1, diarias);
    }

    @Test
    void saidaExatamente12_naoDeveAdicionarDiaria() {
        int diarias = calculadora.calcular(
            LocalDateTime.of(2026, 7, 10, 12, 0),
            LocalDateTime.of(2026, 7, 12, 12, 0)
        );

        assertEquals(2, diarias);
    }

    @Test
    void saidaApos12_deveAdicionarNovaDiaria() {
        int diarias = calculadora.calcular(
            LocalDateTime.of(2026, 7, 10, 12, 0),
            LocalDateTime.of(2026, 7, 12, 12, 1)
        );

        assertEquals(3, diarias);
    }
}
