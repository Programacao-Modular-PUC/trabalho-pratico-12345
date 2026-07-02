package com.hospedagem.notificacao;

import com.hospedagem.model.Aluguel;

public class AluguelCanceladoEvent implements EventoHospedagem {

    private final Aluguel aluguel;

    public AluguelCanceladoEvent(Aluguel aluguel) {
        this.aluguel = aluguel;
    }

    public Aluguel getAluguel() { return aluguel; }

    @Override
    public String getTipo() { return "ALUGUEL_CANCELADO"; }

    @Override
    public String getMensagem() {
        return "Aluguel " + aluguel.getId() + " cancelado.";
    }
}
