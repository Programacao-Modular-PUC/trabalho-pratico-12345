package com.hospedagem.tarifacao;

import com.hospedagem.config.ConfiguracaoGlobalHospedagem;
import java.time.Month;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class TarifacaoBaixaTemporada implements PoliticaTarifacao {

    private static final Set<Month> MESES = Set.of(
        Month.APRIL, Month.MAY, Month.AUGUST, Month.SEPTEMBER
    );
    @Override
    public double aplicar(double valorDiaria, ContextoTarifacao contexto) {
        if (MESES.contains(contexto.getDataEntrada().getMonth())) {
            return valorDiaria * (1 - ConfiguracaoGlobalHospedagem.getInstance().getPercentualBaixaTemporada());
        }
        return valorDiaria;
    }
}
