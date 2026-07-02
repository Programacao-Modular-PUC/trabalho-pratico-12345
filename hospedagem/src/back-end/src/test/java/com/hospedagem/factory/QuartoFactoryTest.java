package com.hospedagem.factory;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.exception.TipoQuartoInvalidoException;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.Residencia;
import com.hospedagem.model.TipoCama;
import com.hospedagem.model.TipoCamaFamilia;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuartoFactoryTest {

    private final QuartoFactory factory = new QuartoFactory();
    private final Residencia residencia = new Residencia();

    @Test
    void criar_deveConstruirCadaSubtipoSemRamificacaoNoService() {
        assertEquals("INDIVIDUAL", factory.criar(individual(), residencia).getTipo());
        assertEquals("DUPLO", factory.criar(duplo(), residencia).getTipo());
        assertEquals("FAMILIA", factory.criar(familia(), residencia).getTipo());
    }

    @Test
    void tipoInvalido_deveLancarExcecaoEspecifica() {
        QuartoDTO dto = comum("TRIPLO");

        assertThrows(TipoQuartoInvalidoException.class, () -> factory.criar(dto, residencia));
    }

    private QuartoDTO individual() {
        QuartoDTO dto = comum("INDIVIDUAL");
        dto.setNumeroDeCamas(1);
        return dto;
    }

    private QuartoDTO duplo() {
        QuartoDTO dto = comum("DUPLO");
        dto.setTipoCama(TipoCama.CASAL);
        return dto;
    }

    private QuartoDTO familia() {
        QuartoDTO dto = comum("FAMILIA");
        dto.setListaDeCamas(List.of(TipoCamaFamilia.CASAL));
        dto.setQuantidadeDeAmbientes(1);
        return dto;
    }

    private QuartoDTO comum(String tipo) {
        QuartoDTO dto = new QuartoDTO();
        dto.setTipo(tipo);
        dto.setValorBase(100.0);
        return dto;
    }
}
