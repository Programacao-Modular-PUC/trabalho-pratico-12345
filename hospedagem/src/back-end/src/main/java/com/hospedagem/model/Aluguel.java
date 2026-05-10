package com.hospedagem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Quarto quarto;

    private LocalDate dataCheckIn;
    private LocalDate dataCheckOut;
    private int numeroPessoas;
    private boolean solicitouBerco;
    private Double valorTotal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public LocalDate getDataCheckIn() { return dataCheckIn; }
    public void setDataCheckIn(LocalDate dataCheckIn) { this.dataCheckIn = dataCheckIn; }

    public LocalDate getDataCheckOut() { return dataCheckOut; }
    public void setDataCheckOut(LocalDate dataCheckOut) { this.dataCheckOut = dataCheckOut; }

    public int getNumeroPessoas() { return numeroPessoas; }
    public void setNumeroPessoas(int numeroPessoas) { this.numeroPessoas = numeroPessoas; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
}
