package com.hospedagem.notificacao;

import org.springframework.stereotype.Component;

@Component
public class CanalEmail extends CanalNotificacaoAbstrato {

    @Override
    public TipoCanalNotificacao getTipo() { return TipoCanalNotificacao.EMAIL; }
}
