package com.hospedagem.notificacao;

import com.hospedagem.exception.CanalNotificacaoNaoRegistradoException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FabricaCanalNotificacao {

    private final Map<TipoCanalNotificacao, CanalNotificacao> canais =
        new EnumMap<>(TipoCanalNotificacao.class);

    public FabricaCanalNotificacao(List<CanalNotificacao> canaisDisponiveis) {
        canaisDisponiveis.forEach(this::registrar);
    }

    public void registrar(CanalNotificacao canal) {
        canais.put(canal.getTipo(), canal);
    }

    public CanalNotificacao criar(TipoCanalNotificacao tipo) {
        CanalNotificacao canal = canais.get(tipo);
        if (canal == null) {
            throw new CanalNotificacaoNaoRegistradoException(tipo);
        }
        return canal;
    }

    public List<CanalNotificacao> listar() {
        return List.copyOf(canais.values());
    }
}
