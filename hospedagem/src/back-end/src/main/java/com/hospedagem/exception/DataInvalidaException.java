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
        super(construirMensagem(dataEntrada, dataSaida));
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
    }

    private static String construirMensagem(LocalDate entrada, LocalDate saida) {
        if (entrada == null || saida == null) {
            return "As datas de entrada e saída não podem ser nulas.";
        }
        if (!entrada.isBefore(LocalDate.now())) {
            // deixa a mensagem genérica; o service detalha o motivo
        }
        if (!saida.isAfter(entrada)) {
            return "Data de saída (" + saida + ") deve ser posterior à data de entrada (" + entrada + ").";
        }
        return "Datas inválidas: entrada " + entrada + ", saída " + saida + ".";
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }
}
