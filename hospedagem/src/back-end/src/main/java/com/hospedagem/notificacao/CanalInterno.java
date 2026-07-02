package com.hospedagem.notificacao;

import org.springframework.stereotype.Component;

@Component
public class CanalInterno extends CanalNotificacaoAbstrato {

    @Override
    public TipoCanalNotificacao getTipo() { return TipoCanalNotificacao.INTERNO; }
}
