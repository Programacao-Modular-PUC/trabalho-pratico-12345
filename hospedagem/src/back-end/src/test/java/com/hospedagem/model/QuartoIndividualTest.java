package com.hospedagem.model;

import com.hospedagem.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuartoIndividualTest {

    private QuartoIndividual quarto;

    @BeforeEach
    void configurar() {
        quarto = new QuartoIndividual();
        quarto.setValorBase(100.0);
        quarto.setNumeroDeCamas(1);
        quarto.setAdicionalPorCama(30.0);
    }

    @Test
    void quartoIndividualUmaCama_deveCobrarSomenteBase() {
        assertEquals(100.0, quarto.calcularDiaria(1, false));
    }

    @Test
    void quartoIndividualCamasExtras_deveSomarAdicional() {
        quarto.setNumeroDeCamas(3);

        assertEquals(160.0, quarto.calcularDiaria(3, false));
    }

    @Test
    void quartoIndividualComBerco_deveLancarRecursoNaoPermitido() {
        assertThrows(
            RecursoNaoPermitidoException.class,
            () -> quarto.calcularDiaria(1, true)
        );
    }

    @Test
    void quartoComArEHidro_deveSomarAdicionais() {
        quarto.setPossuiAR(true);
        quarto.setPossuiHidro(true);

        assertEquals(220.0, quarto.calcularDiaria(1, false));
    }
}
