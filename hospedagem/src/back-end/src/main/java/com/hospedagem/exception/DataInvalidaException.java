package com.hospedagem.exception;

import java.time.LocalDateTime;

public class DataInvalidaException extends IllegalArgumentException {

    private final LocalDateTime dataEntrada;
    private final LocalDateTime dataSaida;

    public DataInvalidaException(String mensagem) {
        super(mensagem);
        this.dataEntrada = null;
        this.dataSaida = null;
    }

    public DataInvalidaException(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        super("Data de saída (" + dataSaida + ") deve ser posterior à data de entrada (" + dataEntrada + ").");
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }
}
