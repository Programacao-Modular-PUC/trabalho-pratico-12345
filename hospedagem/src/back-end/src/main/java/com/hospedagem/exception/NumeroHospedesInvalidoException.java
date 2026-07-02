package com.hospedagem.exception;

public class NumeroHospedesInvalidoException extends IllegalArgumentException {

    public NumeroHospedesInvalidoException() {
        super("Número de hóspedes deve ser maior que zero.");
    }
}
