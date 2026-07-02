package com.hospedagem.exception;

import com.hospedagem.notificacao.TipoCanalNotificacao;

public class CanalNotificacaoNaoRegistradoException extends IllegalArgumentException {

    public CanalNotificacaoNaoRegistradoException(TipoCanalNotificacao tipo) {
        super("Canal de notificação não registrado: " + tipo);
    }
}
