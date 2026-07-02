package com.hospedagem.notificacao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class CentralNotificacoes {

    private final List<CanalNotificacao> observadores = new CopyOnWriteArrayList<>();

    public CentralNotificacoes(FabricaCanalNotificacao fabrica) {
        observadores.addAll(fabrica.listar());
    }

    public void registrar(CanalNotificacao canal) {
        observadores.add(canal);
    }

    public void remover(CanalNotificacao canal) {
        observadores.remove(canal);
    }

    public void publicar(EventoHospedagem evento) {
        observadores.forEach(canal -> canal.notificar(evento));
    }
}
