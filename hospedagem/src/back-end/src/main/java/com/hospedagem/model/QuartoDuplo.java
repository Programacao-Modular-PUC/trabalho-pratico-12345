package com.hospedagem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "quartos_duplos")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    private TipoCama tipoCama;

    // indica se o quarto já inclui berço de fábrica (diferente de "o hóspede pediu berço" no aluguel)
    private boolean possuiBerco;

    @Override
    public double calcularDiaria(int numeroDeHospedes, boolean solicitouBercoNoAluguel) {
        double adicionalTipoCama = switch (tipoCama) {
            case CASAL -> 0.0;
            case QUEEN -> 40.0;
            case KING -> 60.0;
        };

        boolean usarBerco = possuiBerco || solicitouBercoNoAluguel;
        double taxaBerco = usarBerco ? 25.0 : 0.0;

        return getValorBase() + adicionalTipoCama + taxaBerco;
    }

    @Override
    public int calcularLimiteHospedes(boolean solicitouBercoNoAluguel) {
        boolean usarBerco = possuiBerco || solicitouBercoNoAluguel;
        return usarBerco ? 3 : 2;
    }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public boolean isPossuiBerco() { return possuiBerco; }
    public void setPossuiBerco(boolean possuiBerco) { this.possuiBerco = possuiBerco; }
}
