package com.hospedagem.exception;

public class CapacidadeExcedidaException extends RuntimeException {

    private final int capacidadeMaxima;
    private final int hospedesSolicitados;

    public CapacidadeExcedidaException(int capacidadeMaxima, int hospedesSolicitados) {
        super(String.format(
            "Capacidade excedida: o quarto comporta %d hóspede(s), mas foram informados %d.",
            capacidadeMaxima, hospedesSolicitados
        ));
        this.capacidadeMaxima = capacidadeMaxima;
        this.hospedesSolicitados = hospedesSolicitados;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public int getHospedesSolicitados() {
        return hospedesSolicitados;
    }
}
