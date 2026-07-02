package com.hospedagem.notificacao;

import com.hospedagem.model.Aluguel;

public class AluguelCriadoEvent implements EventoHospedagem {

    private final Aluguel aluguel;

    public AluguelCriadoEvent(Aluguel aluguel) {
        this.aluguel = aluguel;
    }

    public Aluguel getAluguel() { return aluguel; }

    @Override
    public String getTipo() { return "ALUGUEL_CRIADO"; }

    @Override
    public String getMensagem() {
        return "Aluguel " + aluguel.getId() + " criado com sucesso.";
    }
}
