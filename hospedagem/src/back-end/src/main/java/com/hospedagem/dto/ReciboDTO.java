package com.hospedagem.dto;

import java.time.LocalDateTime;

public final class ReciboDTO {

    private final LocalDateTime dataEntrada;
    private final LocalDateTime dataSaida;
    private final int numeroDeDiarias;
    private final Double totalAPagar;

    public ReciboDTO(LocalDateTime dataEntrada,
                     LocalDateTime dataSaida,
                     int numeroDeDiarias,
                     Double totalAPagar) {
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.numeroDeDiarias = numeroDeDiarias;
        this.totalAPagar = totalAPagar;
    }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public int getNumeroDeDiarias() { return numeroDeDiarias; }
    public Double getTotalAPagar() { return totalAPagar; }
}
