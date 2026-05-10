package com.hospedagem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AluguelDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long quartoId;

    @NotNull
    private LocalDate dataCheckIn;

    @NotNull
    private LocalDate dataCheckOut;

    @Min(1)
    private int numeroPessoas;

    private boolean solicitouBerco;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public LocalDate getDataCheckIn() { return dataCheckIn; }
    public void setDataCheckIn(LocalDate dataCheckIn) { this.dataCheckIn = dataCheckIn; }

    public LocalDate getDataCheckOut() { return dataCheckOut; }
    public void setDataCheckOut(LocalDate dataCheckOut) { this.dataCheckOut = dataCheckOut; }

    public int getNumeroPessoas() { return numeroPessoas; }
    public void setNumeroPessoas(int numeroPessoas) { this.numeroPessoas = numeroPessoas; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }
}
