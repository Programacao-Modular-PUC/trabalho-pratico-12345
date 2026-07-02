package com.hospedagem.exception;

public class TipoQuartoInvalidoException extends IllegalArgumentException {

    public TipoQuartoInvalidoException(String mensagem) {
        super(mensagem);
    }

    public static TipoQuartoInvalidoException naoInformado() {
        return new TipoQuartoInvalidoException("Tipo de quarto deve ser informado.");
    }

    public static TipoQuartoInvalidoException valorInvalido() {
        return new TipoQuartoInvalidoException(
            "Tipo de quarto inválido. Use INDIVIDUAL, DUPLO ou FAMILIA."
        );
    }
}
