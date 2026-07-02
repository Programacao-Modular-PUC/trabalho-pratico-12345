package com.hospedagem.tarifacao;

import com.hospedagem.config.ConfiguracaoGlobalHospedagem;
import java.time.Month;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class TarifacaoAltaTemporada implements PoliticaTarifacao {

    private static final Set<Month> MESES = Set.of(
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY, Month.JULY
    );
    @Override
    public double aplicar(double valorDiaria, ContextoTarifacao contexto) {
        if (MESES.contains(contexto.getDataEntrada().getMonth())) {
            return valorDiaria * (1 + ConfiguracaoGlobalHospedagem.getInstance().getPercentualAltaTemporada());
        }
        return valorDiaria;
    }
}
