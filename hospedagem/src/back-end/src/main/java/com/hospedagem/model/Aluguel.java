package com.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @JsonIgnoreProperties("residencia")
    private Quarto quarto;

    private LocalDate dataEntrada;
    private LocalDate dataSaida;
    private int numeroDeHospedes;
    private boolean solicitouBerco;
    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    private StatusAluguel status = StatusAluguel.ATIVO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDate dataSaida) { this.dataSaida = dataSaida; }

    public int getNumeroDeHospedes() { return numeroDeHospedes; }
    public void setNumeroDeHospedes(int numeroDeHospedes) { this.numeroDeHospedes = numeroDeHospedes; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public StatusAluguel getStatus() { return status; }
    public void setStatus(StatusAluguel status) { this.status = status; }
}
