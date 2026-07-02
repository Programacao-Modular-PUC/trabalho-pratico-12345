package com.hospedagem.model;

import com.hospedagem.exception.AluguelJaCanceladoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AluguelTest {

    @Test
    void cancelar_deveAlterarEstadoDoAluguel() {
        Aluguel aluguel = new Aluguel();

        aluguel.cancelar();

        assertEquals(StatusAluguel.CANCELADO, aluguel.getStatus());
    }

    @Test
    void cancelarNovamente_deveRejeitarTransicaoInvalida() {
        Aluguel aluguel = new Aluguel();
        aluguel.cancelar();

        assertThrows(AluguelJaCanceladoException.class, aluguel::cancelar);
    }
}
