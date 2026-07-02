package com.hospedagem.service;

import com.hospedagem.model.Pagamento;
import com.hospedagem.model.StatusPagamento;
import com.hospedagem.repository.PagamentoRepository;
import com.hospedagem.notificacao.CentralNotificacoes;
import com.hospedagem.notificacao.PagamentoConfirmadoEvent;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class PagamentoServiceTest {

    @Test
    void confirmarPagamento_devePersistirStatusEData() {
        PagamentoRepository repository = mock(PagamentoRepository.class);
        Pagamento pagamento = new Pagamento();
        pagamento.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamento));
        when(repository.save(pagamento)).thenReturn(pagamento);
        Clock relogio = Clock.fixed(
            Instant.parse("2026-07-01T15:00:00Z"),
            ZoneId.of("America/Sao_Paulo")
        );
        CentralNotificacoes central = mock(CentralNotificacoes.class);
        PagamentoService service = new PagamentoService(repository, relogio, central);

        Pagamento confirmado = service.confirmar(1L);

        assertEquals(StatusPagamento.CONFIRMADO, confirmado.getStatus());
        assertEquals(LocalDateTime.of(2026, 7, 1, 12, 0), confirmado.getDataConfirmacao());
        verify(repository).save(pagamento);
        verify(central).publicar(any(PagamentoConfirmadoEvent.class));
    }
}
