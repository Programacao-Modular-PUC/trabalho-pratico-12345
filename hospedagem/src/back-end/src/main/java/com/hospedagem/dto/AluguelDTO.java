package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AluguelDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long quartoId;

    @NotNull
    @JsonAlias("dataCheckIn")
    private LocalDate dataEntrada;

    @NotNull
    @JsonAlias("dataCheckOut")
    private LocalDate dataSaida;

    @JsonAlias("numeroPessoas")
    @Min(1)
    private int numeroDeHospedes;

    private boolean solicitouBerco;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDate dataSaida) { this.dataSaida = dataSaida; }

    public int getNumeroDeHospedes() { return numeroDeHospedes; }
    public void setNumeroDeHospedes(int numeroDeHospedes) { this.numeroDeHospedes = numeroDeHospedes; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }
}
