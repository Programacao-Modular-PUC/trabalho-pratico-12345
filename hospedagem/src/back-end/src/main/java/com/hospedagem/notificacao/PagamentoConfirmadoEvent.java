package com.hospedagem.notificacao;

import com.hospedagem.model.Pagamento;

public class PagamentoConfirmadoEvent implements EventoHospedagem {

    private final Pagamento pagamento;

    public PagamentoConfirmadoEvent(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public Pagamento getPagamento() { return pagamento; }

    @Override
    public String getTipo() { return "PAGAMENTO_CONFIRMADO"; }

    @Override
    public String getMensagem() {
        return "Pagamento " + pagamento.getId() + " confirmado.";
    }
}
