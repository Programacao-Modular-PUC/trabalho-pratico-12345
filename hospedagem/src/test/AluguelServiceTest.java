package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.*;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.QuartoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Aluguel - Disponibilidade e Validações")
class AluguelServiceTest {

    private AluguelService aluguelService;

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private QuartoRepository quartoRepository;

    // Objetos reutilizáveis
    private Cliente cliente;
    private QuartoDuplo quartoDisponivelComBerco;
    private AluguelDTO dtoValido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aluguelService = new AluguelService(aluguelRepository, clienteRepository, quartoRepository);

        // Setup cliente padrão
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        // Setup quarto padrão (Duplo com berço)
        quartoDisponivelComBerco = new QuartoDuplo();
        quartoDisponivelComBerco.setId(1L);
        quartoDisponivelComBerco.setValorBase(150.0);
        quartoDisponivelComBerco.setTipoCama(TipoCama.QUEEN);
        quartoDisponivelComBerco.setPossuiBerco(false);

        // DTO válido padrão (amanhã por 2 dias)
        dtoValido = new AluguelDTO();
        dtoValido.setClienteId(1L);
        dtoValido.setQuartoId(1L);
        dtoValido.setDataEntrada(LocalDate.now().plusDays(1));
        dtoValido.setDataSaida(LocalDate.now().plusDays(3));
        dtoValido.setNumeroDeHospedes(2);
        dtoValido.setSolicitouBerco(false);
    }

    // ========================================================================
    // SEÇÃO 1: TESTES DE DISPONIBILIDADE
    // ========================================================================

    @DisplayName("Disponibilidade do Quarto")
    class DisponibilidadeTest {

        @Test
        @DisplayName("Deve criar aluguel para quarto disponível")
        void testCriarAluguelQuartoDisponivel() {
            // Arrange
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Aluguel não deve ser nulo");
            assertEquals(cliente.getId(), aluguel.getCliente().getId(), "Cliente deve ser o mesmo");
            assertEquals(quartoDisponivelComBerco.getId(), aluguel.getQuarto().getId(), "Quarto deve ser o mesmo");
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando quarto está indisponível no período")
        void testQuartoIndisponivelPeriodoConflitante() {
            // Arrange
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(true); // Simulando conflito

            // Act & Assert
            QuartoIndisponivelException exception = assertThrows(
                QuartoIndisponivelException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve lançar exceção para quarto indisponível"
            );

            assertTrue(exception.getMessage().contains("já existe reserva"));
        }

        @Test
        @DisplayName("Deve validar conflito ao atualizar aluguel")
        void testConflitoPeriodoAoAtualizar() {
            // Arrange
            Aluguel aluguelExistente = new Aluguel();
            aluguelExistente.setId(1L);
            aluguelExistente.setCliente(cliente);
            aluguelExistente.setQuarto(quartoDisponivelComBerco);

            when(aluguelRepository.findById(1L)).thenReturn(Optional.of(aluguelExistente));
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodoExcluindo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida(),
                    1L
            )).thenReturn(true); // Conflito

            // Act & Assert
            assertThrows(
                QuartoIndisponivelException.class,
                () -> aluguelService.atualizar(1L, dtoValido),
                "Deve lançar exceção ao atualizar com conflito de período"
            );
        }

        @Test
        @DisplayName("Deve permitir atualizar aluguel sem conflito, excluindo o próprio")
        void testAtualizarAluguelSemConflito() {
            // Arrange
            Aluguel aluguelExistente = new Aluguel();
            aluguelExistente.setId(1L);
            aluguelExistente.setCliente(cliente);
            aluguelExistente.setQuarto(quartoDisponivelComBerco);
            aluguelExistente.setDataEntrada(dtoValido.getDataEntrada());
            aluguelExistente.setDataSaida(dtoValido.getDataSaida());

            when(aluguelRepository.findById(1L)).thenReturn(Optional.of(aluguelExistente));
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodoExcluindo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida(),
                    1L
            )).thenReturn(false); // Sem conflito
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguelAtualizado = aluguelService.atualizar(1L, dtoValido);

            // Assert
            assertNotNull(aluguelAtualizado, "Aluguel atualizado não deve ser nulo");
            verify(aluguelRepository, times(1)).save(any(Aluguel.class));
        }
    }

    // ========================================================================
    // SEÇÃO 2: TESTES DE VALIDAÇÃO DE DATAS
    // ========================================================================

    @DisplayName("Validação de Datas")
    class ValidacaoDatasTest {

        @Test
        @DisplayName("Deve rejeitar data de entrada nula")
        void testDataEntradaNula() {
            // Arrange
            dtoValido.setDataEntrada(null);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                NullPointerException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar data de entrada nula"
            );
        }

        @Test
        @DisplayName("Deve rejeitar data de saída nula")
        void testDataSaidaNula() {
            // Arrange
            dtoValido.setDataSaida(null);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                NullPointerException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar data de saída nula"
            );
        }

        @Test
        @DisplayName("Deve rejeitar data de entrada no passado")
        void testDataEntradaNoPassado() {
            // Arrange
            dtoValido.setDataEntrada(LocalDate.now().minusDays(1));

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                DataInvalidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar data de entrada no passado"
            );
        }

        @Test
        @DisplayName("Deve rejeitar data de entrada hoje")
        void testDataEntradaHoje() {
            // Arrange
            dtoValido.setDataEntrada(LocalDate.now());
            dtoValido.setDataSaida(LocalDate.now().plusDays(2));

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            DataInvalidaException exception = assertThrows(
                DataInvalidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar data de entrada hoje"
            );

            assertTrue(exception.getMessage().contains("a partir de hoje"));
        }

        @Test
        @DisplayName("Deve aceitar data de entrada a partir de amanhã")
        void testDataEntradaAmanha() {
            // Arrange
            dtoValido.setDataEntrada(LocalDate.now().plusDays(1));
            dtoValido.setDataSaida(LocalDate.now().plusDays(3));

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Deve aceitar data de entrada a partir de amanhã");
        }

        @Test
        @DisplayName("Deve rejeitar data de saída igual à data de entrada")
        void testDataSaidaIgualEntrada() {
            // Arrange
            LocalDate data = LocalDate.now().plusDays(1);
            dtoValido.setDataEntrada(data);
            dtoValido.setDataSaida(data);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                DataInvalidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Data de saída deve ser depois da data de entrada"
            );
        }

        @Test
        @DisplayName("Deve rejeitar data de saída anterior à data de entrada")
        void testDataSaidaAnterior() {
            // Arrange
            LocalDate dataEntrada = LocalDate.now().plusDays(5);
            dtoValido.setDataEntrada(dataEntrada);
            dtoValido.setDataSaida(dataEntrada.minusDays(2));

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                DataInvalidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Data de saída não pode ser anterior à entrada"
            );
        }

        @Test
        @DisplayName("Deve aceitar períodos longos (semanas/meses)")
        void testPeriodoLongo() {
            // Arrange
            dtoValido.setDataEntrada(LocalDate.now().plusDays(1));
            dtoValido.setDataSaida(LocalDate.now().plusDays(30)); // 29 noites

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Deve aceitar períodos longos");
        }
    }

    // ========================================================================
    // SEÇÃO 3: TESTES DE NÚMERO DE HÓSPEDES
    // ========================================================================

    @DisplayName("Validação de Número de Hóspedes")
    class NumeroHospedesTest {

        @Test
        @DisplayName("Deve rejeitar 0 hóspedes")
        void testZeroHospedes() {
            // Arrange
            dtoValido.setNumeroDeHospedes(0);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar 0 hóspedes"
            );
        }

        @Test
        @DisplayName("Deve rejeitar número negativo de hóspedes")
        void testNumerNegativoHospedes() {
            // Arrange
            dtoValido.setNumeroDeHospedes(-1);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar número negativo"
            );
        }

        @Test
        @DisplayName("Deve aceitar 1 hóspede")
        void testUmHospede() {
            // Arrange
            dtoValido.setNumeroDeHospedes(1);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quartoDisponivelComBerco));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Deve aceitar 1 hóspede");
        }
    }

    // ========================================================================
    // SEÇÃO 4: TESTES DE CAPACIDADE DO QUARTO
    // ========================================================================

    @DisplayName("Validação de Capacidade do Quarto")
    class CapacidadeQuartoTest {

        @Test
        @DisplayName("Deve rejeitar aluguel quando hóspedes excedem capacidade (sem berço)")
        void testCapacidadeExcedidaSemBerco() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.CASAL);
            quarto.setPossuiBerco(false); // Limite = 2

            dtoValido.setNumeroDeHospedes(3); // Tentando 3 hóspedes
            dtoValido.setSolicitouBerco(false);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));

            // Act & Assert
            CapacidadeExcedidaException exception = assertThrows(
                CapacidadeExcedidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar quando hóspedes excedem capacidade"
            );

            assertTrue(exception.getMessage().contains("3"));
            assertTrue(exception.getMessage().contains("2"));
        }

        @Test
        @DisplayName("Deve rejeitar quando hóspedes excedem capacidade (com berço)")
        void testCapacidadeExcedidaComBerco() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.CASAL);
            quarto.setPossuiBerco(false);

            dtoValido.setNumeroDeHospedes(4); // Tentando 4 hóspedes
            dtoValido.setSolicitouBerco(true); // Limite com berço = 3

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));

            // Act & Assert
            assertThrows(
                CapacidadeExcedidaException.class,
                () -> aluguelService.criar(dtoValido),
                "Deve rejeitar 4 hóspedes quando limite é 3"
            );
        }

        @Test
        @DisplayName("Deve aceitar quando hóspedes estão no limite exato")
        void testCapacidadeNoLimiteExato() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.CASAL);
            quarto.setPossuiBerco(false);

            dtoValido.setNumeroDeHospedes(2); // Limite sem berço = 2

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Deve aceitar quando no limite exato");
        }

        @Test
        @DisplayName("Deve aceitar com berço quando hóspedes estão no limite")
        void testCapacidadeComBercoNoLimite() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.CASAL);
            quarto.setPossuiBerco(false);

            dtoValido.setNumeroDeHospedes(3); // Limite com berço = 3
            dtoValido.setSolicitouBerco(true);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            assertNotNull(aluguel, "Deve aceitar com berço no limite");
        }
    }

    // ========================================================================
    // SEÇÃO 5: TESTES PARAMETRIZADOS
    // ========================================================================

    @DisplayName("Testes Parametrizados - Múltiplos Cenários")
    class TestesParametrizadosTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 5, 10, 20})
        @DisplayName("Deve validar diferentes quantidades de hóspedes válidas")
        void testDiferentesQuantidadesValidas(int quantidade) {
            // Arrange
            QuartoFamilia quarto = new QuartoFamilia();
            quarto.setId(1L);
            quarto.setValorBase(300.0);
            quarto.setListaDeCamas(java.util.Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL
            )); // Capacidade = 20

            dtoValido.setNumeroDeHospedes(quantidade);

            if (quantidade > 0 && quantidade <= 20) {
                when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
                when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
                when(aluguelRepository.existeConflitoDePeriodo(
                        1L,
                        dtoValido.getDataEntrada(),
                        dtoValido.getDataSaida()
                )).thenReturn(false);
                when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

                // Act
                Aluguel aluguel = aluguelService.criar(dtoValido);

                // Assert
                assertNotNull(aluguel, "Deve criar aluguel para " + quantidade + " hóspedes");
            }
        }
    }

    // ========================================================================
    // SEÇÃO 6: TESTE DE CÁLCULO DE VALOR TOTAL
    // ========================================================================

    @DisplayName("Cálculo do Valor Total do Aluguel")
    class CalculoValorTotalTest {

        @Test
        @DisplayName("Deve calcular valor total corretamente")
        void testCalculoValorTotal() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.QUEEN);
            quarto.setPossuiBerco(false);

            dtoValido.setDataEntrada(LocalDate.now().plusDays(1));
            dtoValido.setDataSaida(LocalDate.now().plusDays(4)); // 3 diárias
            dtoValido.setNumeroDeHospedes(2);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            // Diária = 150 + 40 (QUEEN) + 0 (sem berço) = 190
            // Total = 190 * 3 = 570
            assertEquals(570.0, aluguel.getValorTotal(), 0.01, "Valor total deve ser 570.0");
        }

        @Test
        @DisplayName("Deve calcular valor total com berço incluído")
        void testCalculoValorTotalComBerco() {
            // Arrange
            QuartoDuplo quarto = new QuartoDuplo();
            quarto.setId(1L);
            quarto.setValorBase(150.0);
            quarto.setTipoCama(TipoCama.CASAL);
            quarto.setPossuiBerco(false);

            dtoValido.setDataEntrada(LocalDate.now().plusDays(1));
            dtoValido.setDataSaida(LocalDate.now().plusDays(3)); // 2 diárias
            dtoValido.setNumeroDeHospedes(3);
            dtoValido.setSolicitouBerco(true);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(quartoRepository.findById(1L)).thenReturn(Optional.of(quarto));
            when(aluguelRepository.existeConflitoDePeriodo(
                    1L,
                    dtoValido.getDataEntrada(),
                    dtoValido.getDataSaida()
            )).thenReturn(false);
            when(aluguelRepository.save(any(Aluguel.class))).thenAnswer(i -> i.getArgument(0));

            // Act
            Aluguel aluguel = aluguelService.criar(dtoValido);

            // Assert
            // Diária = 150 + 0 (CASAL) + 25 (berço) = 175
            // Total = 175 * 2 = 350
            assertEquals(350.0, aluguel.getValorTotal(), 0.01, "Valor total com berço deve ser 350.0");
        }
    }
}
