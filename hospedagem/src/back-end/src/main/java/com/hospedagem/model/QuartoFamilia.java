package com.hospedagem.model;

import com.hospedagem.exception.NegocioException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_familia")
public class QuartoFamilia extends Quarto {

    private int camasSolteiro;
    private int camasCasal;
    private int camasQueenKing;
    private int quantidadeAmbientes;

    @Override
    public double calcularDiaria(int numeroPessoas, boolean solicitouBerco) {
        validarConfiguracao();

        int capacidade = calcularCapacidade();

        if (numeroPessoas < 1) {
            throw new NegocioException("O número de hóspedes deve ser maior que zero.");
        }

        if (numeroPessoas > capacidade) {
            throw new NegocioException(
                "Número de hóspedes (" + numeroPessoas + ") excede a capacidade máxima do quarto família (" + capacidade + ")."
            );
        }

        double valor;
        if (numeroPessoas <= 2) {
            valor = getValorBase();
        } else if (numeroPessoas <= 4) {
            valor = getValorBase() * 1.20;
        } else if (numeroPessoas <= 6) {
            valor = getValorBase() * 1.40;
        } else {
            valor = getValorBase() * 1.60;
        }

        double desconto = 0.0;
        if (numeroPessoas == 4) {
            desconto = 0.05;
        } else if (numeroPessoas == 6) {
            desconto = 0.10;
        } else if (numeroPessoas >= 8) {
            desconto = 0.15;
        }

        return valor * (1 - desconto);
    }

    private void validarConfiguracao() {
        if (camasSolteiro < 0 || camasCasal < 0 || camasQueenKing < 0) {
            throw new NegocioException("A quantidade de camas não pode ser negativa.");
        }

        if (quantidadeAmbientes < 1) {
            throw new NegocioException("Quarto família deve ter pelo menos 1 ambiente.");
        }

        if (calcularCapacidade() < 1) {
            throw new NegocioException("Quarto família deve ter pelo menos uma cama configurada.");
        }
    }

    private int calcularCapacidade() {
        return camasSolteiro + camasCasal * 2 + camasQueenKing * 2;
    }

    public int getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(int camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public int getCamasCasal() { return camasCasal; }
    public void setCamasCasal(int camasCasal) { this.camasCasal = camasCasal; }

    public int getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(int camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public int getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(int quantidadeAmbientes) { this.quantidadeAmbientes = quantidadeAmbientes; }
}
