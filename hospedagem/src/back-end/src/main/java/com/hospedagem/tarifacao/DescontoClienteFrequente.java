package com.hospedagem.tarifacao;

import com.hospedagem.config.ConfiguracaoGlobalHospedagem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(300)
public class DescontoClienteFrequente implements PoliticaTarifacao {

    private static final long MINIMO_ALUGUEIS = 3;
    @Override
    public double aplicar(double valorDiaria, ContextoTarifacao contexto) {
        if (contexto.getQuantidadeAlugueisAnteriores() >= MINIMO_ALUGUEIS) {
            return valorDiaria * (1 - ConfiguracaoGlobalHospedagem.getInstance().getPercentualClienteFrequente());
        }
        return valorDiaria;
    }
}
