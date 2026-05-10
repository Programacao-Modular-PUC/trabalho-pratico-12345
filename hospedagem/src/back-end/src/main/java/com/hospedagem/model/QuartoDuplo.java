package com.hospedagem.model;

import com.hospedagem.exception.NegocioException;
import jakarta.persistence.*;

@Entity
@Table(name = "quartos_duplos")
public class QuartoDuplo extends Quarto {

    @Enumerated(EnumType.STRING)
    private TipoCama tipoCama;

    private boolean possuiBerco;

    @Override
    public double calcularDiaria(int numeroPessoas, boolean solicitouBerco) {
        if (solicitouBerco && !possuiBerco) {
            throw new NegocioException("Este quarto duplo não possui berço disponível.");
        }
        double adicionalCama = switch (tipoCama) {
            case CASAL -> 0.0;
            case QUEEN -> 80.0;
            case KING  -> 150.0;
        };
        double taxaBerco = solicitouBerco ? 40.0 : 0.0;
        return getValorBase() + adicionalCama + taxaBerco;
    }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public boolean isPossuiBerco() { return possuiBerco; }
    public void setPossuiBerco(boolean possuiBerco) { this.possuiBerco = possuiBerco; }
}
