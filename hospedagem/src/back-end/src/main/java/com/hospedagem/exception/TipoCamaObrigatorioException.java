package com.hospedagem.exception;

public class TipoCamaObrigatorioException extends IllegalArgumentException {

    public TipoCamaObrigatorioException() {
        super("Quarto duplo precisa informar tipoCama: CASAL, QUEEN ou KING.");
    }
}
