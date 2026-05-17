package com.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valorBase;
    private boolean possuiAR;
    private boolean possuiHidro;

    @ManyToOne
    @JoinColumn(name = "residencia_id")
    @JsonIgnoreProperties("quartos")
    private Residencia residencia;

    public abstract double calcularDiaria(int numeroDeHospedes, boolean solicitouBerco);

    public abstract int calcularLimiteHospedes(boolean solicitouBerco);

    public String getTipo() {
        if (this instanceof QuartoIndividual) {
            return "INDIVIDUAL";
        }
        if (this instanceof QuartoDuplo) {
            return "DUPLO";
        }
        if (this instanceof QuartoFamilia) {
            return "FAMILIA";
        }
        throw new IllegalStateException("Tipo de quarto desconhecido: " + this.getClass().getSimpleName());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAR() { return possuiAR; }
    public void setPossuiAR(boolean possuiAR) { this.possuiAR = possuiAR; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Residencia getResidencia() { return residencia; }
    public void setResidencia(Residencia residencia) { this.residencia = residencia; }
}
