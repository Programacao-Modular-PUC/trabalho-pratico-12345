package com.hospedagem.repository;

import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.Residencia;
import com.hospedagem.model.StatusAluguel;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("h2")
class AluguelRepositoryTest {

    @Autowired
    private AluguelRepository aluguelRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private QuartoRepository quartoRepository;
    @Autowired
    private ResidenciaRepository residenciaRepository;

    private Cliente cliente;
    private QuartoIndividual quarto;

    @BeforeEach
    void configurar() {
        Residencia residencia = new Residencia();
        residencia.setNome("Residência Teste");
        residencia.setEndereco("Rua Teste");
        residencia = residenciaRepository.save(residencia);

        cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678900");
        cliente = clienteRepository.save(cliente);

        quarto = new QuartoIndividual();
        quarto.setValorBase(100.0);
        quarto.setNumeroDeCamas(1);
        quarto.setAdicionalPorCama(30.0);
        quarto.setResidencia(residencia);
        quarto = quartoRepository.save(quarto);
    }

    @Test
    void aluguelCancelado_naoDeveBloquearNovoPeriodo() {
        salvarAluguel(StatusAluguel.CANCELADO);

        boolean conflito = aluguelRepository.existeConflitoDePeriodo(
            quarto.getId(), dataEntrada(), dataSaida()
        );

        assertFalse(conflito);
    }

    @Test
    void criarAluguelEmPeriodoAdjacente_devePermitir() {
        salvarAluguel(StatusAluguel.ATIVO);

        boolean conflito = aluguelRepository.existeConflitoDePeriodo(
            quarto.getId(),
            dataSaida(),
            dataSaida().plusDays(1)
        );

        assertFalse(conflito);
    }

    @Test
    void historicoPorResidencia_deveRetornarSomenteQuartosDaResidencia() {
        salvarAluguel(StatusAluguel.ATIVO);

        assertEquals(
            1,
            aluguelRepository.findByQuartoResidenciaId(quarto.getResidencia().getId()).size()
        );
    }

    private void salvarAluguel(StatusAluguel status) {
        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dataEntrada());
        aluguel.setDataSaida(dataSaida());
        aluguel.setNumeroDeHospedes(1);
        aluguel.setNumeroDeDiarias(2);
        aluguel.setValorTotal(200.0);
        aluguel.setStatus(status);
        aluguelRepository.saveAndFlush(aluguel);
    }

    private LocalDateTime dataEntrada() {
        return LocalDateTime.of(2026, 7, 10, 12, 0);
    }

    private LocalDateTime dataSaida() {
        return LocalDateTime.of(2026, 7, 12, 12, 0);
    }
}
