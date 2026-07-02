package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.dto.ReciboDTO;
import com.hospedagem.exception.PagamentoConfirmadoImpedeCancelamentoException;
import com.hospedagem.exception.AluguelJaCanceladoException;
import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.Pagamento;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.Residencia;
import com.hospedagem.model.StatusAluguel;
import com.hospedagem.model.StatusPagamento;
import com.hospedagem.notificacao.AluguelCanceladoEvent;
import com.hospedagem.notificacao.AluguelCriadoEvent;
import com.hospedagem.notificacao.CentralNotificacoes;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.PagamentoRepository;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import com.hospedagem.tarifacao.TarifacaoPadrao;
import jakarta.persistence.EntityNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AluguelServiceTest {

    @Mock
    private AluguelRepository aluguelRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private QuartoRepository quartoRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private ResidenciaRepository residenciaRepository;
    @Mock
    private CentralNotificacoes centralNotificacoes;

    private AluguelService service;

    @BeforeEach
    void configurar() {
        Clock relogio = Clock.fixed(
            Instant.parse("2026-07-01T15:00:00Z"),
            ZoneId.of("America/Sao_Paulo")
        );
        service = new AluguelService(
            aluguelRepository,
            clienteRepository,
            quartoRepository,
            pagamentoRepository,
            residenciaRepository,
            new CalculadoraDiarias(),
            new ServicoTarifacao(List.of(new TarifacaoPadrao()), aluguelRepository),
            centralNotificacoes,
            relogio
        );
    }

    @Test
    void criarAluguel_deveGerarPagamentoPendente() {
        Cliente cliente = cliente(1L);
        QuartoIndividual quarto = quarto(2L);
        AluguelDTO dto = aluguelDTO();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto));
        when(aluguelRepository.existeConflitoDePeriodo(any(), any(), any())).thenReturn(false);
        when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(invocacao -> {
            Aluguel aluguel = invocacao.getArgument(0);
            aluguel.setId(10L);
            return aluguel;
        });
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocacao -> {
            Pagamento pagamento = invocacao.getArgument(0);
            pagamento.setId(20L);
            return pagamento;
        });

        Aluguel aluguel = service.criar(dto);

        assertNotNull(aluguel.getPagamento());
        assertEquals(StatusPagamento.PENDENTE, aluguel.getPagamento().getStatus());
        assertEquals(aluguel.getValorTotal(), aluguel.getPagamento().getValor());
        assertSame(aluguel, aluguel.getPagamento().getAluguel());
        verify(pagamentoRepository).save(any(Pagamento.class));
        verify(centralNotificacoes).publicar(any(AluguelCriadoEvent.class));
    }

    @Test
    void gerarRecibo_deveRetornarOsQuatroCamposExigidos() {
        Aluguel aluguel = aluguelPersistido();
        Pagamento pagamento = new Pagamento();
        pagamento.setValor(200.0);
        when(aluguelRepository.findById(10L)).thenReturn(Optional.of(aluguel));
        when(pagamentoRepository.findByAluguelId(10L)).thenReturn(Optional.of(pagamento));

        ReciboDTO recibo = service.gerarRecibo(10L);

        assertEquals(aluguel.getDataEntrada(), recibo.getDataEntrada());
        assertEquals(aluguel.getDataSaida(), recibo.getDataSaida());
        assertEquals(2, recibo.getNumeroDeDiarias());
        assertEquals(200.0, recibo.getTotalAPagar());
    }

    @Test
    void historicoPorResidencia_deveFiltrarPelaResidencia() {
        Residencia residencia = new Residencia();
        residencia.setId(7L);
        List<Aluguel> esperado = List.of(aluguelPersistido());
        when(residenciaRepository.findById(7L)).thenReturn(Optional.of(residencia));
        when(aluguelRepository.findByQuartoResidenciaId(7L)).thenReturn(esperado);

        assertSame(esperado, service.listarPorResidencia(7L));
    }

    @Test
    void cancelarComPagamentoConfirmado_deveRejeitarOperacao() {
        Aluguel aluguel = aluguelPersistido();
        Pagamento pagamento = new Pagamento();
        pagamento.setStatus(StatusPagamento.CONFIRMADO);
        aluguel.setPagamento(pagamento);
        when(aluguelRepository.findById(10L)).thenReturn(Optional.of(aluguel));

        assertThrows(
            PagamentoConfirmadoImpedeCancelamentoException.class,
            () -> service.cancelar(10L)
        );
    }

    @Test
    void cancelarAluguelAtivo_devePersistirStatusCancelado() {
        Aluguel aluguel = aluguelPersistido();
        when(aluguelRepository.findById(10L)).thenReturn(Optional.of(aluguel));
        when(aluguelRepository.save(aluguel)).thenReturn(aluguel);

        Aluguel cancelado = service.cancelar(10L);

        assertEquals(StatusAluguel.CANCELADO, cancelado.getStatus());
        verify(aluguelRepository).save(aluguel);
        verify(centralNotificacoes).publicar(any(AluguelCanceladoEvent.class));
    }

    @Test
    void cancelarAluguelJaCancelado_deveRejeitarOperacao() {
        Aluguel aluguel = aluguelPersistido();
        aluguel.cancelar();
        when(aluguelRepository.findById(10L)).thenReturn(Optional.of(aluguel));

        assertThrows(AluguelJaCanceladoException.class, () -> service.cancelar(10L));
    }

    @Test
    void cancelarAluguelInexistente_deveRetornar404() {
        when(aluguelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.cancelar(999L));
    }

    @Test
    void historicoPorCliente_deveRetornarSomenteAlugueisDoCliente() {
        Cliente cliente = cliente(1L);
        List<Aluguel> esperado = List.of(aluguelPersistido());
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(esperado);

        assertSame(esperado, service.listarPorCliente(1L));
        verify(aluguelRepository).findByClienteId(1L);
    }

    @Test
    void historicoPorClienteSemAlugueis_deveRetornarListaVazia() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente(1L)));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(List.of());

        assertEquals(List.of(), service.listarPorCliente(1L));
    }

    @Test
    void historicoPorClienteInexistente_deveRetornar404() {
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.listarPorCliente(999L));
    }

    @Test
    void historicoPorCliente_deveIncluirCancelados() {
        Aluguel cancelado = aluguelPersistido();
        cancelado.cancelar();
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente(1L)));
        when(aluguelRepository.findByClienteId(1L)).thenReturn(List.of(cancelado));

        assertEquals(StatusAluguel.CANCELADO, service.listarPorCliente(1L).get(0).getStatus());
    }

    @Test
    void criarAluguelComCapacidadeExcedida_deveFalhar() {
        AluguelDTO dto = aluguelDTO();
        dto.setNumeroDeHospedes(3);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente(1L)));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto(2L)));
        when(aluguelRepository.existeConflitoDePeriodo(any(), any(), any())).thenReturn(false);

        assertThrows(CapacidadeExcedidaException.class, () -> service.criar(dto));
    }

    @Test
    void criarAluguelComPeriodoSobreposto_deveFalhar() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente(1L)));
        when(quartoRepository.findById(2L)).thenReturn(Optional.of(quarto(2L)));
        when(aluguelRepository.existeConflitoDePeriodo(any(), any(), any())).thenReturn(true);

        assertThrows(QuartoIndisponivelException.class, () -> service.criar(aluguelDTO()));
    }

    private Cliente cliente(Long id) {
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }

    private QuartoIndividual quarto(Long id) {
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setId(id);
        quarto.setValorBase(100.0);
        quarto.setNumeroDeCamas(2);
        quarto.setAdicionalPorCama(30.0);
        return quarto;
    }

    private AluguelDTO aluguelDTO() {
        AluguelDTO dto = new AluguelDTO();
        dto.setClienteId(1L);
        dto.setQuartoId(2L);
        dto.setDataEntrada(LocalDateTime.of(2026, 7, 10, 12, 0));
        dto.setDataSaida(LocalDateTime.of(2026, 7, 12, 12, 0));
        dto.setNumeroDeHospedes(2);
        return dto;
    }

    private Aluguel aluguelPersistido() {
        Aluguel aluguel = new Aluguel();
        aluguel.setId(10L);
        aluguel.setStatus(StatusAluguel.ATIVO);
        aluguel.setDataEntrada(LocalDateTime.of(2026, 7, 10, 12, 0));
        aluguel.setDataSaida(LocalDateTime.of(2026, 7, 12, 12, 0));
        aluguel.setNumeroDeDiarias(2);
        aluguel.setValorTotal(200.0);
        return aluguel;
    }
}
