package com.hospedagem.notificacao;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FabricaCanalNotificacaoTest {

    @Test
    void criar_deveRetornarCanalRegistradoPorTipo() {
        FabricaCanalNotificacao fabrica = new FabricaCanalNotificacao(List.of(
            new CanalEmail(), new CanalSMS(), new CanalWhatsApp(), new CanalInterno()
        ));

        assertInstanceOf(CanalEmail.class, fabrica.criar(TipoCanalNotificacao.EMAIL));
        assertInstanceOf(CanalSMS.class, fabrica.criar(TipoCanalNotificacao.SMS));
        assertInstanceOf(CanalWhatsApp.class, fabrica.criar(TipoCanalNotificacao.WHATSAPP));
        assertInstanceOf(CanalInterno.class, fabrica.criar(TipoCanalNotificacao.INTERNO));
    }
}
