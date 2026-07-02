package com.hospedagem.exception;

public class NumeroCamasInvalidoException extends IllegalArgumentException {

    public NumeroCamasInvalidoException() {
        super("Quarto individual precisa ter pelo menos 1 cama.");
    }
}
