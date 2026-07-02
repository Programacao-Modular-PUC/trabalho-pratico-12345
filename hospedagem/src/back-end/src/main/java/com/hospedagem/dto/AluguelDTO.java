package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AluguelDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long quartoId;

    @NotNull
    @JsonAlias("dataCheckIn")
    private LocalDateTime dataEntrada;

    @NotNull
    @JsonAlias("dataCheckOut")
    private LocalDateTime dataSaida;

    @JsonAlias("numeroPessoas")
    @Min(1)
    private int numeroDeHospedes;

    private boolean solicitouBerco;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }

    public int getNumeroDeHospedes() { return numeroDeHospedes; }
    public void setNumeroDeHospedes(int numeroDeHospedes) { this.numeroDeHospedes = numeroDeHospedes; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }
}
