package com.hospedagem.service;

import com.hospedagem.factory.QuartoFactory;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.QuartoFamilia;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuartoServiceTest {

    @Test
    void listarQuartosPorTipo_deveRetornarSomenteTipoSolicitado() {
        QuartoRepository repository = mock(QuartoRepository.class);
        ResidenciaRepository residenciaRepository = mock(ResidenciaRepository.class);
        QuartoService service = new QuartoService(
            repository,
            residenciaRepository,
            new QuartoFactory()
        );
        when(repository.findAll()).thenReturn(List.of(
            new QuartoIndividual(),
            new QuartoDuplo(),
            new QuartoFamilia()
        ));

        List<Quarto> resultado = service.listarPorTipo("familia");

        assertEquals(1, resultado.size());
        assertEquals("FAMILIA", resultado.get(0).getTipo());
    }
}
