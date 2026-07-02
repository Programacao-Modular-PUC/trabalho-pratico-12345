package com.hospedagem.model;

import com.hospedagem.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuartoDuploTest {

    private QuartoDuplo quarto;

    @BeforeEach
    void configurar() {
        quarto = new QuartoDuplo();
        quarto.setValorBase(100.0);
        quarto.setTipoCama(TipoCama.CASAL);
    }

    @Test
    void quartoDuplo_deveAplicarAdicionalPorTipoCama() {
        assertEquals(100.0, quarto.calcularDiaria(2, false));

        quarto.setTipoCama(TipoCama.QUEEN);
        assertEquals(140.0, quarto.calcularDiaria(2, false));

        quarto.setTipoCama(TipoCama.KING);
        assertEquals(160.0, quarto.calcularDiaria(2, false));
    }

    @Test
    void quartoDuploComBerco_deveAplicarTaxaEAumentarCapacidade() {
        quarto.setPossuiBerco(true);

        assertEquals(125.0, quarto.calcularDiaria(3, true));
        assertEquals(3, quarto.calcularLimiteHospedes(true));
    }

    @Test
    void quartoDuploQueApenasOfereceBerco_naoDeveCobrarTaxa() {
        quarto.setPossuiBerco(true);

        assertEquals(100.0, quarto.calcularDiaria(2, false));
        assertEquals(2, quarto.calcularLimiteHospedes(false));
    }

    @Test
    void quartoDuploSemOfertaComBercoSolicitado_deveLancarExcecao() {
        assertThrows(
            RecursoNaoPermitidoException.class,
            () -> quarto.calcularDiaria(3, true)
        );
    }
}
