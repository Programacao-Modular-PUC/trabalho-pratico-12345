package com.hospedagem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResidenciaTest {

    @Test
    void adicionarQuarto_deveManterOsDoisLadosDaAssociacao() {
        Residencia residencia = new Residencia();
        QuartoIndividual quarto = new QuartoIndividual();

        residencia.adicionarQuarto(quarto);

        assertTrue(residencia.getQuartos().contains(quarto));
        assertSame(residencia, quarto.getResidencia());
    }
}
