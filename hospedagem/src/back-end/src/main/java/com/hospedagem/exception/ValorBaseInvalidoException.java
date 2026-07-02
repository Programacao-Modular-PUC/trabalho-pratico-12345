package com.hospedagem.exception;

public class ValorBaseInvalidoException extends IllegalArgumentException {

    public ValorBaseInvalidoException() {
        super("Valor base deve ser maior ou igual a zero.");
    }
}
