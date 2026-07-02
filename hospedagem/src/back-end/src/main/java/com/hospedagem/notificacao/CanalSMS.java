package com.hospedagem.notificacao;

import org.springframework.stereotype.Component;

@Component
public class CanalSMS extends CanalNotificacaoAbstrato {

    @Override
    public TipoCanalNotificacao getTipo() { return TipoCanalNotificacao.SMS; }
}
