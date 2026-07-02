package com.hospedagem.tarifacao;

import com.hospedagem.config.ConfiguracaoGlobalHospedagem;
import java.time.MonthDay;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class TarifacaoFeriado implements PoliticaTarifacao {

    private static final Set<MonthDay> FERIADOS = Set.of(
        MonthDay.of(1, 1), MonthDay.of(4, 21), MonthDay.of(5, 1),
        MonthDay.of(9, 7), MonthDay.of(10, 12), MonthDay.of(11, 2),
        MonthDay.of(11, 15), MonthDay.of(12, 25)
    );
    @Override
    public double aplicar(double valorDiaria, ContextoTarifacao contexto) {
        if (FERIADOS.contains(MonthDay.from(contexto.getDataEntrada()))) {
            return valorDiaria * (1 + ConfiguracaoGlobalHospedagem.getInstance().getPercentualFeriado());
        }
        return valorDiaria;
    }
}
