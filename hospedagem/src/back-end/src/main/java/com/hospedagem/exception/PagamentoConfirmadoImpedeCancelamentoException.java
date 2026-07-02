package com.hospedagem.exception;

public class PagamentoConfirmadoImpedeCancelamentoException extends IllegalStateException {

    public PagamentoConfirmadoImpedeCancelamentoException() {
        super("Aluguel com pagamento confirmado não pode ser cancelado.");
    }
}
