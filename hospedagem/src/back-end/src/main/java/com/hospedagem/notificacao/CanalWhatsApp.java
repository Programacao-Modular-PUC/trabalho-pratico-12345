package com.hospedagem.notificacao;

import org.springframework.stereotype.Component;

@Component
public class CanalWhatsApp extends CanalNotificacaoAbstrato {

    @Override
    public TipoCanalNotificacao getTipo() { return TipoCanalNotificacao.WHATSAPP; }
}
