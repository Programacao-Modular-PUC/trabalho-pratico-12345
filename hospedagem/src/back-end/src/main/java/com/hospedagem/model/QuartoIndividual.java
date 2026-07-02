package com.hospedagem.model;

import com.hospedagem.exception.RecursoNaoPermitidoException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_individuais")
public class QuartoIndividual extends Quarto {

    private int numeroDeCamas;
    private Double adicionalPorCama = 30.0;

    @Override
    public String getTipo() {
        return "INDIVIDUAL";
    }

    @Override
    public double calcularDiaria(int numeroDeHospedes, boolean solicitouBerco) {
        if (solicitouBerco) {
            throw new RecursoNaoPermitidoException(
                "berço",
                "Quarto Individual",
                "quartos individuais não acomodam berço"
            );
        }
        return getValorBase()
            + (adicionalPorCama * (numeroDeCamas - 1))
            + calcularAdicionaisComuns();
    }

    @Override
    public int calcularLimiteHospedes(boolean solicitouBerco) {
        if (solicitouBerco) {
            throw new RecursoNaoPermitidoException(
                "berço",
                "Quarto Individual",
                "quartos individuais não acomodam berço"
            );
        }
        return numeroDeCamas;
    }

    @Override
    public Integer getNumeroDeCamas() { return numeroDeCamas; }
    public void setNumeroDeCamas(int numeroDeCamas) { this.numeroDeCamas = numeroDeCamas; }

    @Override
    public Double getAdicionalPorCama() { return adicionalPorCama; }
    public void setAdicionalPorCama(Double adicionalPorCama) { this.adicionalPorCama = adicionalPorCama; }
}
