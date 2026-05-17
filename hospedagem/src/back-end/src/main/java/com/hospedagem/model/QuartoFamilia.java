package com.hospedagem.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quartos_familia")
public class QuartoFamilia extends Quarto {

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<TipoCamaFamilia> listaDeCamas = new ArrayList<>();

    private int quantidadeDeAmbientes;

    @Override
    public double calcularDiaria(int numeroDeHospedes, boolean solicitouBerco) {
        // calcula adicional por quantidade de hospedes
        double valor = getValorBase() * (1 + (numeroDeHospedes * 0.08));
        double desconto = calcularDesconto(numeroDeHospedes);

        return valor * (1 - desconto);
    }

    @Override
    public int calcularLimiteHospedes(boolean solicitouBerco) {
        return getCapacidadeMaxima();
    }

    public int getCapacidadeMaxima() {
        if (listaDeCamas == null) {
            return 0;
        }

        int total = 0;
        for (TipoCamaFamilia cama : listaDeCamas) {
            if (cama == TipoCamaFamilia.SOLTEIRO) {
                total += 1;
            } else {
                total += 2;
            }
        }
        return total;
    }

    private double calcularDesconto(int numeroDeHospedes) {
        if (numeroDeHospedes >= 8) {
            return 0.15;
        }
        if (numeroDeHospedes >= 6) {
            return 0.10;
        }
        if (numeroDeHospedes >= 4) {
            return 0.05;
        }
        return 0.0;
    }

    public List<TipoCamaFamilia> getListaDeCamas() { return listaDeCamas; }
    public void setListaDeCamas(List<TipoCamaFamilia> listaDeCamas) { this.listaDeCamas = listaDeCamas; }

    public int getQuantidadeDeAmbientes() { return quantidadeDeAmbientes; }
    public void setQuantidadeDeAmbientes(int quantidadeDeAmbientes) { this.quantidadeDeAmbientes = quantidadeDeAmbientes; }
}
