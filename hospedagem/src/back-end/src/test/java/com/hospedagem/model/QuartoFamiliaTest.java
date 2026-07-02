package com.hospedagem.model;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuartoFamiliaTest {

    private QuartoFamilia quarto;

    @BeforeEach
    void configurar() {
        quarto = new QuartoFamilia();
        quarto.setValorBase(100.0);
    }

    @Test
    void quartoFamilia_deveCalcularCapacidadePelasCamas() {
        quarto.setListaDeCamas(List.of(
            TipoCamaFamilia.SOLTEIRO,
            TipoCamaFamilia.CASAL,
            TipoCamaFamilia.QUEEN
        ));

        assertEquals(5, quarto.getCapacidadeMaxima());
    }

    @Test
    void quartoFamilia_deveAplicarFaixasDeDesconto() {
        assertEquals(124.0, quarto.calcularDiaria(3, false), 0.001);
        assertEquals(125.4, quarto.calcularDiaria(4, false), 0.001);
        assertEquals(133.2, quarto.calcularDiaria(6, false), 0.001);
        assertEquals(139.4, quarto.calcularDiaria(8, false), 0.001);
    }
}
