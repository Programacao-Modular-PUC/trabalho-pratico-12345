package com.hospedagem.notificacao;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public abstract class CanalNotificacaoAbstrato implements CanalNotificacao {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final List<String> mensagens = new CopyOnWriteArrayList<>();

    @Override
    public void notificar(EventoHospedagem evento) {
        String mensagem = "[" + getTipo() + "] " + evento.getMensagem();
        mensagens.add(mensagem);
        logger.info(mensagem);
    }

    public List<String> getMensagens() {
        return Collections.unmodifiableList(mensagens);
    }
}
