package com.hospedagem.exception;

public class ListaCamasObrigatoriaException extends IllegalArgumentException {

    public ListaCamasObrigatoriaException() {
        super("Quarto família precisa ter uma listaDeCamas.");
    }
}
