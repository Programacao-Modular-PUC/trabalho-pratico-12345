package com.hospedagem.exception;

public class AlteracaoTipoQuartoNaoPermitidaException extends IllegalStateException {

    public AlteracaoTipoQuartoNaoPermitidaException() {
        super("Não é possível alterar o tipo do quarto.");
    }
}
