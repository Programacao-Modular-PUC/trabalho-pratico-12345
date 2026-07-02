package com.hospedagem.service;

import com.hospedagem.model.Pagamento;
import com.hospedagem.repository.PagamentoRepository;
import com.hospedagem.notificacao.CentralNotificacoes;
import com.hospedagem.notificacao.PagamentoConfirmadoEvent;
import jakarta.persistence.EntityNotFoundException;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final Clock relogio;
    private final CentralNotificacoes centralNotificacoes;

    public PagamentoService(PagamentoRepository repository,
                            Clock relogio,
                            CentralNotificacoes centralNotificacoes) {
        this.repository = repository;
        this.relogio = relogio;
        this.centralNotificacoes = centralNotificacoes;
    }

    public Pagamento buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Pagamento não encontrado para o id: " + id
            ));
    }

    public Pagamento confirmar(Long id) {
        Pagamento pagamento = buscarPorId(id);
        pagamento.confirmar(LocalDateTime.now(relogio));
        Pagamento pagamentoSalvo = repository.save(pagamento);
        centralNotificacoes.publicar(new PagamentoConfirmadoEvent(pagamentoSalvo));
        return pagamentoSalvo;
    }
}
