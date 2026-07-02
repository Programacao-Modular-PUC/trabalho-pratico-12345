package com.hospedagem.exception;

public class QuartoObrigatorioException extends IllegalArgumentException {

    public QuartoObrigatorioException() {
        super("Quarto deve ser informado.");
    }
}
