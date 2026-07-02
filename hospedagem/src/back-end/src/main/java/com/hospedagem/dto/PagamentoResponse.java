package com.hospedagem.dto;

import com.hospedagem.model.Pagamento;
import com.hospedagem.model.StatusPagamento;
import java.time.LocalDateTime;

public class PagamentoResponse {

    private final Long id;
    private final Double valor;
    private final StatusPagamento status;
    private final LocalDateTime dataConfirmacao;

    public PagamentoResponse(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.valor = pagamento.getValor();
        this.status = pagamento.getStatus();
        this.dataConfirmacao = pagamento.getDataConfirmacao();
    }

    public Long getId() { return id; }
    public Double getValor() { return valor; }
    public StatusPagamento getStatus() { return status; }
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
}
