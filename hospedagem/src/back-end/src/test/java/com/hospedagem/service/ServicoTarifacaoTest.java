package com.hospedagem.service;

import com.hospedagem.model.Cliente;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.tarifacao.ContextoTarifacao;
import com.hospedagem.tarifacao.DescontoClienteFrequente;
import com.hospedagem.tarifacao.PoliticaTarifacao;
import com.hospedagem.tarifacao.TarifacaoAltaTemporada;
import com.hospedagem.tarifacao.TarifacaoFeriado;
import com.hospedagem.tarifacao.TarifacaoPadrao;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ServicoTarifacaoTest {

    @Test
    void composicaoDePoliticas_deveAplicarTodasEmSequencia() {
        List<PoliticaTarifacao> politicas = List.of(
            new TarifacaoAltaTemporada(),
            new TarifacaoFeriado(),
            new DescontoClienteFrequente()
        );
        ServicoTarifacao service = new ServicoTarifacao(
            politicas,
            mock(AluguelRepository.class)
        );
        Cliente cliente = new Cliente();
        ContextoTarifacao contexto = new ContextoTarifacao(
            LocalDateTime.of(2026, 12, 25, 12, 0),
            LocalDateTime.of(2026, 12, 26, 12, 0),
            cliente,
            3
        );

        assertEquals(124.2, service.aplicarPoliticas(100.0, contexto), 0.001);
    }

    @Test
    void tarifacaoPadrao_devePreservarValorPolimorficoDoQuarto() {
        AluguelRepository repository = mock(AluguelRepository.class);
        ServicoTarifacao service = new ServicoTarifacao(
            List.of(new TarifacaoPadrao()),
            repository
        );
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(100.0);
        quarto.setNumeroDeCamas(2);
        quarto.setAdicionalPorCama(30.0);
        Cliente cliente = new Cliente();

        double valor = service.calcular(
            quarto,
            2,
            false,
            LocalDateTime.of(2026, 3, 10, 12, 0),
            LocalDateTime.of(2026, 3, 11, 12, 0),
            cliente
        );

        assertEquals(130.0, valor);
    }
}
