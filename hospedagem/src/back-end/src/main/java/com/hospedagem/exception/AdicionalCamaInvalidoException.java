package com.hospedagem.exception;

public class AdicionalCamaInvalidoException extends IllegalArgumentException {

    public AdicionalCamaInvalidoException() {
        super("Adicional por cama não pode ser negativo.");
    }
}
