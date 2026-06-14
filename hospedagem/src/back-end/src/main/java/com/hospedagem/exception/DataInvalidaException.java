package com.hospedagem.exception;

import java.time.LocalDate;

public class DataInvalidaException extends RuntimeException {

    private final LocalDate dataEntrada;
    private final LocalDate dataSaida;

    public DataInvalidaException(String mensagem) {
        super(mensagem);
        this.dataEntrada = null;
        this.dataSaida = null;
    }

    public DataInvalidaException(LocalDate dataEntrada, LocalDate dataSaida) {
        super("Data de saída (" + dataSaida + ") deve ser posterior à data de entrada (" + dataEntrada + ").");
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }
}
