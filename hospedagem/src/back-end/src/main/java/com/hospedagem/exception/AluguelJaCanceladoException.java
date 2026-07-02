package com.hospedagem.exception;

public class AluguelJaCanceladoException extends IllegalStateException {

    public AluguelJaCanceladoException() {
        super("Aluguel já está cancelado.");
    }
}
