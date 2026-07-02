package com.hospedagem.notificacao;

public interface CanalNotificacao {

    TipoCanalNotificacao getTipo();

    void notificar(EventoHospedagem evento);
}
