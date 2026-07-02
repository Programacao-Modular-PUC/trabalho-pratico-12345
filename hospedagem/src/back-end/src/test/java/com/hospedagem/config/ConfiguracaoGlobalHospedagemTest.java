package com.hospedagem.config;

import com.hospedagem.service.CalculadoraDiarias;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConfiguracaoGlobalHospedagemTest {

    @Test
    void getInstance_deveRetornarSempreAMesmaInstancia() {
        ConfiguracaoGlobalHospedagem primeira = ConfiguracaoGlobalHospedagem.getInstance();
        ConfiguracaoGlobalHospedagem segunda = ConfiguracaoGlobalHospedagem.getInstance();

        assertSame(primeira, segunda);
    }

    @Test
    void calculoDasDiarias_deveLerHorarioBaseDoSingleton() {
        ConfiguracaoGlobalHospedagem configuracao = ConfiguracaoGlobalHospedagem.getInstance();
        CalculadoraDiarias calculadora = new CalculadoraDiarias();

        assertEquals(LocalTime.NOON, configuracao.getHorarioBaseDiaria());
        assertEquals(2, calculadora.calcular(
            LocalDateTime.of(2026, 7, 10, 12, 0),
            LocalDateTime.of(2026, 7, 11, 12, 1)
        ));
    }
}
