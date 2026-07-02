package com.hospedagem.notificacao;

import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Pagamento;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CentralNotificacoesTest {

    @Test
    void observerFake_deveReceberOsTresEventos() {
        CentralNotificacoes central = centralVazia();
        CanalFake canal = new CanalFake();
        central.registrar(canal);
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        Pagamento pagamento = new Pagamento();
        pagamento.setId(2L);

        central.publicar(new AluguelCriadoEvent(aluguel));
        central.publicar(new AluguelCanceladoEvent(aluguel));
        central.publicar(new PagamentoConfirmadoEvent(pagamento));

        assertEquals(
            List.of("ALUGUEL_CRIADO", "ALUGUEL_CANCELADO", "PAGAMENTO_CONFIRMADO"),
            canal.tiposRecebidos
        );
    }

    @Test
    void adicionarNovoCanal_deveExigirApenasRegistroDoObserver() {
        CentralNotificacoes central = centralVazia();
        CanalFake canal = new CanalFake();

        central.registrar(canal);
        Aluguel aluguel = new Aluguel();
        aluguel.setId(1L);
        central.publicar(new AluguelCriadoEvent(aluguel));

        assertEquals(1, canal.tiposRecebidos.size());
    }

    private CentralNotificacoes centralVazia() {
        return new CentralNotificacoes(new FabricaCanalNotificacao(List.of()));
    }

    static class CanalFake implements CanalNotificacao {

        private final List<String> tiposRecebidos = new ArrayList<>();

        @Override
        public TipoCanalNotificacao getTipo() { return TipoCanalNotificacao.INTERNO; }

        @Override
        public void notificar(EventoHospedagem evento) {
            tiposRecebidos.add(evento.getTipo());
        }
    }
}
