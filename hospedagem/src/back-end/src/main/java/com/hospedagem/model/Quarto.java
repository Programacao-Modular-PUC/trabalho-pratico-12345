package com.hospedagem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valorBase;
    private boolean possuiAr;
    private boolean possuiHidro;

    public abstract double calcularDiaria(int numeroPessoas, boolean solicitouBerco);

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAr() { return possuiAr; }
    public void setPossuiAr(boolean possuiAr) { this.possuiAr = possuiAr; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }
}
