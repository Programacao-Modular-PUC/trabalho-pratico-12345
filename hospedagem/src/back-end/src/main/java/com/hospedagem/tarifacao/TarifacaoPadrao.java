package com.hospedagem.tarifacao;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class TarifacaoPadrao implements PoliticaTarifacao {

    @Override
    public double aplicar(double valorDiaria, ContextoTarifacao contexto) {
        return valorDiaria;
    }
}
