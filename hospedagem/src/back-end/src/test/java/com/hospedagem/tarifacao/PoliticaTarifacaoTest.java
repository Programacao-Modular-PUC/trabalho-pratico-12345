package com.hospedagem.tarifacao;

import com.hospedagem.model.Cliente;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PoliticaTarifacaoTest {

    @Test
    void tarifacaoPadrao_deveManterValor() {
        assertEquals(100.0, new TarifacaoPadrao().aplicar(100.0, contexto(3, 0)));
    }

    @Test
    void tarifacaoAltaTemporada_deveAcrescentarVintePorCento() {
        assertEquals(120.0, new TarifacaoAltaTemporada().aplicar(100.0, contexto(7, 0)));
    }

    @Test
    void tarifacaoBaixaTemporada_deveDescontarDezPorCento() {
        assertEquals(90.0, new TarifacaoBaixaTemporada().aplicar(100.0, contexto(5, 0)));
    }

    @Test
    void tarifacaoFeriado_deveAcrescentarQuinzePorCento() {
        assertEquals(115.0, new TarifacaoFeriado().aplicar(100.0, contexto(12, 0, 25)), 0.001);
    }

    @Test
    void descontoClienteFrequente_deveDescontarDezPorCento() {
        assertEquals(90.0, new DescontoClienteFrequente().aplicar(100.0, contexto(3, 3)));
    }

    private ContextoTarifacao contexto(int mes, long historico) {
        return contexto(mes, historico, 10);
    }

    private ContextoTarifacao contexto(int mes, long historico, int dia) {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        return new ContextoTarifacao(
            LocalDateTime.of(2026, mes, dia, 12, 0),
            LocalDateTime.of(2026, mes, dia + 1, 12, 0),
            cliente,
            historico
        );
    }
}
