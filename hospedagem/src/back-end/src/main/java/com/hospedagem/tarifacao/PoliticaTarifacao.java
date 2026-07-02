package com.hospedagem.tarifacao;

public interface PoliticaTarifacao {

    double aplicar(double valorDiaria, ContextoTarifacao contexto);
}
