package com.hospedagem.exception;

public class PagamentoJaConfirmadoException extends IllegalStateException {

    public PagamentoJaConfirmadoException() {
        super("Pagamento já está confirmado.");
    }
}
