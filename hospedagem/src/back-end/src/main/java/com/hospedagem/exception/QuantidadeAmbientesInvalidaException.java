package com.hospedagem.exception;

public class QuantidadeAmbientesInvalidaException extends IllegalArgumentException {

    public QuantidadeAmbientesInvalidaException() {
        super("Quarto família precisa ter pelo menos 1 ambiente.");
    }
}
