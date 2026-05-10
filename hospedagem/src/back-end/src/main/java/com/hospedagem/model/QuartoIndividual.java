package com.hospedagem.model;

import com.hospedagem.exception.NegocioException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_individuais")
public class QuartoIndividual extends Quarto {

    private int numeroCamas;

    @Override
    public double calcularDiaria(int numeroPessoas, boolean solicitouBerco) {
        if (solicitouBerco) {
            throw new NegocioException("Quarto individual não permite berço.");
        }
        if (numeroPessoas > numeroCamas) {
            throw new NegocioException(
                "Número de hóspedes (" + numeroPessoas + ") excede a capacidade do quarto individual (" + numeroCamas + ")."
            );
        }
        if (numeroCamas == 1) {
            return getValorBase();
        }
        return getValorBase() + (numeroCamas - 1) * 50.0;
    }

    public int getNumeroCamas() { return numeroCamas; }
    public void setNumeroCamas(int numeroCamas) { this.numeroCamas = numeroCamas; }
}
