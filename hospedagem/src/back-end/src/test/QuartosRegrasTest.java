package com.hospedagem.model;

import com.hospedagem.exception.NegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuartoRegrasTest {

    @Test
    void deveCalcularDiariaDoQuartoIndividualComAdicionalPorCama() {
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(200.0);
        quarto.setNumeroCamas(3);

        double diaria = quarto.calcularDiaria(2, false);

        assertEquals(300.0, diaria);
    }

    @Test
    void quartoIndividualNaoDevePermitirBerco() {
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(200.0);
        quarto.setNumeroCamas(1);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(1, true));
    }

    @Test
    void quartoIndividualNaoDevePermitirHospedesAcimaDaQuantidadeDeCamas() {
        QuartoIndividual quarto = new QuartoIndividual();
        quarto.setValorBase(200.0);
        quarto.setNumeroCamas(1);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(2, false));
    }

    @Test
    void deveCalcularDiariaDoQuartoDuploComCamaQueenEBerco() {
        QuartoDuplo quarto = new QuartoDuplo();
        quarto.setValorBase(300.0);
        quarto.setTipoCama(TipoCama.QUEEN);
        quarto.setPossuiBerco(true);

        double diaria = quarto.calcularDiaria(2, true);

        assertEquals(420.0, diaria);
    }

    @Test
    void quartoDuploNaoDevePermitirMaisDeDoisHospedes() {
        QuartoDuplo quarto = new QuartoDuplo();
        quarto.setValorBase(300.0);
        quarto.setTipoCama(TipoCama.CASAL);
        quarto.setPossuiBerco(false);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(3, false));
    }

    @Test
    void quartoDuploNaoDevePermitirBercoQuandoNaoDisponivel() {
        QuartoDuplo quarto = new QuartoDuplo();
        quarto.setValorBase(300.0);
        quarto.setTipoCama(TipoCama.CASAL);
        quarto.setPossuiBerco(false);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(2, true));
    }

    @Test
    void deveCalcularDiariaDoQuartoFamiliaComPercentualEDesconto() {
        QuartoFamilia quarto = new QuartoFamilia();
        quarto.setValorBase(500.0);
        quarto.setCamasSolteiro(2);
        quarto.setCamasCasal(1);
        quarto.setCamasQueenKing(1);
        quarto.setQuantidadeAmbientes(2);

        double diaria = quarto.calcularDiaria(4, false);

        assertEquals(570.0, diaria);
    }

    @Test
    void quartoFamiliaNaoDevePermitirHospedesAcimaDaCapacidade() {
        QuartoFamilia quarto = new QuartoFamilia();
        quarto.setValorBase(500.0);
        quarto.setCamasSolteiro(2);
        quarto.setCamasCasal(1);
        quarto.setCamasQueenKing(1);
        quarto.setQuantidadeAmbientes(2);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(7, false));
    }

    @Test
    void quartoFamiliaNaoDevePermitirConfiguracaoSemCamas() {
        QuartoFamilia quarto = new QuartoFamilia();
        quarto.setValorBase(500.0);
        quarto.setCamasSolteiro(0);
        quarto.setCamasCasal(0);
        quarto.setCamasQueenKing(0);
        quarto.setQuantidadeAmbientes(1);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(1, false));
    }

    @Test
    void quartoFamiliaNaoDevePermitirConfiguracaoSemAmbientes() {
        QuartoFamilia quarto = new QuartoFamilia();
        quarto.setValorBase(500.0);
        quarto.setCamasSolteiro(2);
        quarto.setCamasCasal(1);
        quarto.setCamasQueenKing(0);
        quarto.setQuantidadeAmbientes(0);

        assertThrows(NegocioException.class, () -> quarto.calcularDiaria(2, false));
    }
}
