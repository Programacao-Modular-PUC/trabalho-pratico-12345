package com.hospedagem.service;

import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.RecursoNaoPermitidoException;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.QuartoFamilia;
import com.hospedagem.model.TipoCama;
import com.hospedagem.model.TipoCamaFamilia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Quartos - Diária, Berço, Capacidade e Disponibilidade")
class QuartoServiceTest {

    // ========================================================================
    // SEÇÃO 1: TESTES PARA QUARTO INDIVIDUAL
    // ========================================================================

    @DisplayName("QuartoIndividual - Cálculo de Diária")
    class QuartoIndividualDiariaTest {

        private QuartoIndividual quartoIndividual;

        @BeforeEach
        void setUp() {
            quartoIndividual = new QuartoIndividual();
            quartoIndividual.setValorBase(100.0);
            quartoIndividual.setNumeroDeCamas(1);
            quartoIndividual.setAdicionalPorCama(30.0);
        }

        @Test
        @DisplayName("Deve calcular diária corretamente com 1 cama")
        void testCalcularDiaria1Cama() {
            // Arrange
            int numeroDeHospedes = 1;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 100 + (30 * (1 - 1)) = 100
            assertEquals(100.0, diaria, "Diária com 1 cama deve ser o valor base");
        }

        @Test
        @DisplayName("Deve calcular diária corretamente com 2 camas")
        void testCalcularDiaria2Camas() {
            // Arrange
            quartoIndividual.setNumeroDeCamas(2);
            int numeroDeHospedes = 2;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 100 + (30 * (2 - 1)) = 100 + 30 = 130
            assertEquals(130.0, diaria, "Diária com 2 camas deve ser valorBase + adicional");
        }

        @Test
        @DisplayName("Deve calcular diária corretamente com 3 camas")
        void testCalcularDiaria3Camas() {
            // Arrange
            quartoIndividual.setNumeroDeCamas(3);
            int numeroDeHospedes = 3;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 100 + (30 * (3 - 1)) = 100 + 60 = 160
            assertEquals(160.0, diaria, "Diária com 3 camas deve ser valorBase + 2x adicional");
        }

        @Test
        @DisplayName("Deve respeitar o adicional customizado por cama")
        void testCalcularDiariaComAdicionalCustomizado() {
            // Arrange
            quartoIndividual.setNumeroDeCamas(2);
            quartoIndividual.setAdicionalPorCama(50.0);
            int numeroDeHospedes = 2;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 100 + (50 * (2 - 1)) = 150
            assertEquals(150.0, diaria, "Deve usar o valor customizado de adicional");
        }
    }

    // ========================================================================
    // SEÇÃO 2: TESTES PARA REGRAS DE BERÇO - QUARTO INDIVIDUAL
    // ========================================================================

    @DisplayName("QuartoIndividual - Regras de Berço")
    class QuartoIndividualBercoTest {

        private QuartoIndividual quartoIndividual;

        @BeforeEach
        void setUp() {
            quartoIndividual = new QuartoIndividual();
            quartoIndividual.setValorBase(100.0);
            quartoIndividual.setNumeroDeCamas(1);
        }

        @Test
        @DisplayName("Deve lançar exceção ao calcular diária com berço")
        void testCalcularDiariaComBerco() {
            // Arrange
            int numeroDeHospedes = 1;
            boolean solicitouBerco = true;

            // Act & Assert
            RecursoNaoPermitidoException exception = assertThrows(
                RecursoNaoPermitidoException.class,
                () -> quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco),
                "Deve lançar exceção ao solicitar berço em quarto individual"
            );

            assertTrue(exception.getMessage().contains("berço"));
            assertTrue(exception.getMessage().contains("Quarto Individual"));
        }

        @Test
        @DisplayName("Deve lançar exceção ao calcular limite com berço")
        void testCalcularLimiteComBerco() {
            // Arrange
            boolean solicitouBerco = true;

            // Act & Assert
            RecursoNaoPermitidoException exception = assertThrows(
                RecursoNaoPermitidoException.class,
                () -> quartoIndividual.calcularLimiteHospedes(solicitouBerco),
                "Deve lançar exceção ao tentar calcular limite com berço"
            );

            assertTrue(exception.getMessage().contains("berço"));
        }

        @Test
        @DisplayName("Deve calcular limite corretamente sem berço")
        void testCalcularLimiteSemBerco() {
            // Arrange
            quartoIndividual.setNumeroDeCamas(2);
            boolean solicitouBerco = false;

            // Act
            int limite = quartoIndividual.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(2, limite, "Limite deve ser igual ao número de camas");
        }
    }

    // ========================================================================
    // SEÇÃO 3: TESTES PARA QUARTO DUPLO - CÁLCULO DE DIÁRIA
    // ========================================================================

    @DisplayName("QuartoDuplo - Cálculo de Diária")
    class QuartoDuploDiariaTest {

        private QuartoDuplo quartoDuplo;

        @BeforeEach
        void setUp() {
            quartoDuplo = new QuartoDuplo();
            quartoDuplo.setValorBase(150.0);
        }

        @Test
        @DisplayName("Deve calcular diária com cama CASAL sem berço")
        void testCalcularDiariaCamasalSemBerco() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.CASAL);
            quartoDuplo.setPossuiBerco(false);
            int numeroDeHospedes = 2;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 0 + 0 = 150
            assertEquals(150.0, diaria, "CASAL sem berço não deve adicionar extras");
        }

        @Test
        @DisplayName("Deve calcular diária com cama QUEEN sem berço")
        void testCalcularDiariaQueenSemBerco() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.QUEEN);
            quartoDuplo.setPossuiBerco(false);
            int numeroDeHospedes = 2;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 40 + 0 = 190
            assertEquals(190.0, diaria, "QUEEN deve adicionar 40.0");
        }

        @Test
        @DisplayName("Deve calcular diária com cama KING sem berço")
        void testCalcularDiariaKingSemBerco() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.KING);
            quartoDuplo.setPossuiBerco(false);
            int numeroDeHospedes = 2;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 60 + 0 = 210
            assertEquals(210.0, diaria, "KING deve adicionar 60.0");
        }

        @Test
        @DisplayName("Deve calcular diária com CASAL e berço de fábrica")
        void testCalcularDiariaCamasalComBercoFabrica() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.CASAL);
            quartoDuplo.setPossuiBerco(true); // Berço de fábrica
            int numeroDeHospedes = 3;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 0 + 25 = 175
            assertEquals(175.0, diaria, "Com berço de fábrica deve adicionar taxa de 25.0");
        }

        @Test
        @DisplayName("Deve calcular diária com CASAL e berço solicitado no aluguel")
        void testCalcularDiariaCamasalComBercoSolicitado() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.CASAL);
            quartoDuplo.setPossuiBerco(false);
            int numeroDeHospedes = 3;
            boolean solicitouBerco = true; // Berço solicitado no aluguel

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 0 + 25 = 175
            assertEquals(175.0, diaria, "Com berço solicitado deve adicionar taxa de 25.0");
        }

        @Test
        @DisplayName("Deve calcular diária com QUEEN + berço solicitado")
        void testCalcularDiariaQueenComBercoSolicitado() {
            // Arrange
            quartoDuplo.setTipoCama(TipoCama.QUEEN);
            quartoDuplo.setPossuiBerco(false);
            int numeroDeHospedes = 3;
            boolean solicitouBerco = true;

            // Act
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 150 + 40 + 25 = 215
            assertEquals(215.0, diaria, "QUEEN + berço deve somar todos os adicionais");
        }
    }

    // ========================================================================
    // SEÇÃO 4: TESTES PARA QUARTO DUPLO - LIMITES E BERÇO
    // ========================================================================

    @DisplayName("QuartoDuplo - Limites de Hóspedes")
    class QuartoDuploLimiteTest {

        private QuartoDuplo quartoDuplo;

        @BeforeEach
        void setUp() {
            quartoDuplo = new QuartoDuplo();
            quartoDuplo.setTipoCama(TipoCama.CASAL);
        }

        @Test
        @DisplayName("Deve permitir 2 hóspedes sem berço")
        void testLimite2SemBerco() {
            // Arrange
            quartoDuplo.setPossuiBerco(false);
            boolean solicitouBerco = false;

            // Act
            int limite = quartoDuplo.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(2, limite, "Sem berço, limite deve ser 2");
        }

        @Test
        @DisplayName("Deve permitir 3 hóspedes com berço de fábrica")
        void testLimite3ComBercoFabrica() {
            // Arrange
            quartoDuplo.setPossuiBerco(true);
            boolean solicitouBerco = false;

            // Act
            int limite = quartoDuplo.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(3, limite, "Com berço de fábrica, limite deve ser 3");
        }

        @Test
        @DisplayName("Deve permitir 3 hóspedes com berço solicitado")
        void testLimite3ComBercoSolicitado() {
            // Arrange
            quartoDuplo.setPossuiBerco(false);
            boolean solicitouBerco = true;

            // Act
            int limite = quartoDuplo.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(3, limite, "Com berço solicitado, limite deve ser 3");
        }

        @Test
        @DisplayName("Deve permitir 3 hóspedes com ambos os tipos de berço")
        void testLimite3ComAmbosOsBercos() {
            // Arrange
            quartoDuplo.setPossuiBerco(true);
            boolean solicitouBerco = true;

            // Act
            int limite = quartoDuplo.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(3, limite, "Com ambos os berços, limite deve ser 3");
        }
    }

    // ========================================================================
    // SEÇÃO 5: TESTES PARA QUARTO FAMÍLIA - CÁLCULO DE DIÁRIA
    // ========================================================================

    @DisplayName("QuartoFamilia - Cálculo de Diária com Desconto")
    class QuartoFamiliaDiariaTest {

        private QuartoFamilia quartoFamilia;

        @BeforeEach
        void setUp() {
            quartoFamilia = new QuartoFamilia();
            quartoFamilia.setValorBase(200.0);
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO
            ));
        }

        @Test
        @DisplayName("Deve calcular diária sem desconto para 1 hóspede")
        void testCalcularDiaria1Hospede() {
            // Arrange
            int numeroDeHospedes = 1;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (1 * 0.08)) * (1 - 0) = 200 * 1.08 = 216
            assertEquals(216.0, diaria, 0.01, "1 hóspede sem desconto");
        }

        @Test
        @DisplayName("Deve calcular diária sem desconto para 3 hóspedes")
        void testCalcularDiaria3Hospedes() {
            // Arrange
            int numeroDeHospedes = 3;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (3 * 0.08)) * (1 - 0) = 200 * 1.24 = 248
            assertEquals(248.0, diaria, 0.01, "3 hóspedes sem desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 5% para 4-5 hóspedes")
        void testCalcularDiaria4Hospedes() {
            // Arrange
            int numeroDeHospedes = 4;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (4 * 0.08)) * (1 - 0.05) = 200 * 1.32 * 0.95 = 250.8
            assertEquals(250.8, diaria, 0.01, "4 hóspedes com 5% desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 5% para 5 hóspedes")
        void testCalcularDiaria5Hospedes() {
            // Arrange
            int numeroDeHospedes = 5;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (5 * 0.08)) * (1 - 0.05) = 200 * 1.4 * 0.95 = 266
            assertEquals(266.0, diaria, 0.01, "5 hóspedes com 5% desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 10% para 6-7 hóspedes")
        void testCalcularDiaria6Hospedes() {
            // Arrange
            int numeroDeHospedes = 6;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (6 * 0.08)) * (1 - 0.10) = 200 * 1.48 * 0.90 = 266.4
            assertEquals(266.4, diaria, 0.01, "6 hóspedes com 10% desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 10% para 7 hóspedes")
        void testCalcularDiaria7Hospedes() {
            // Arrange
            int numeroDeHospedes = 7;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (7 * 0.08)) * (1 - 0.10) = 200 * 1.56 * 0.90 = 280.8
            assertEquals(280.8, diaria, 0.01, "7 hóspedes com 10% desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 15% para 8+ hóspedes")
        void testCalcularDiaria8Hospedes() {
            // Arrange
            int numeroDeHospedes = 8;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (8 * 0.08)) * (1 - 0.15) = 200 * 1.64 * 0.85 = 278.8
            assertEquals(278.8, diaria, 0.01, "8 hóspedes com 15% desconto");
        }

        @Test
        @DisplayName("Deve calcular diária com desconto de 15% para 10 hóspedes")
        void testCalcularDiaria10Hospedes() {
            // Arrange
            int numeroDeHospedes = 10;
            boolean solicitouBerco = false;

            // Act
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            // Esperado: 200 * (1 + (10 * 0.08)) * (1 - 0.15) = 200 * 1.80 * 0.85 = 306
            assertEquals(306.0, diaria, 0.01, "10 hóspedes com 15% desconto");
        }
    }

    // ========================================================================
    // SEÇÃO 6: TESTES PARA QUARTO FAMÍLIA - LIMITES E CAPACIDADE
    // ========================================================================

    @DisplayName("QuartoFamilia - Limites de Hóspedes")
    class QuartoFamiliaLimiteTest {

        private QuartoFamilia quartoFamilia;

        @BeforeEach
        void setUp() {
            quartoFamilia = new QuartoFamilia();
        }

        @Test
        @DisplayName("Deve calcular capacidade máxima com camas mistas")
        void testCapacidadeMaximaMistas() {
            // Arrange: 2 camas CASAL + 2 SOLTEIRO = 2*2 + 2*1 = 6
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO
            ));

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();

            // Assert
            assertEquals(6, capacidade, "2 CASAL + 2 SOLTEIRO = 6 pessoas");
        }

        @Test
        @DisplayName("Deve calcular capacidade máxima com apenas camas CASAL")
        void testCapacidadeMaximaApenasCAasal() {
            // Arrange: 4 camas CASAL = 4*2 = 8
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL
            ));

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();

            // Assert
            assertEquals(8, capacidade, "4 CASAL = 8 pessoas");
        }

        @Test
        @DisplayName("Deve calcular capacidade máxima com apenas camas SOLTEIRO")
        void testCapacidadeMaximaApenasSOLTEIRO() {
            // Arrange: 6 camas SOLTEIRO = 6*1 = 6
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO
            ));

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();

            // Assert
            assertEquals(6, capacidade, "6 SOLTEIRO = 6 pessoas");
        }

        @Test
        @DisplayName("Deve retornar 0 para lista vazia")
        void testCapacidadeMaximaListaVazia() {
            // Arrange
            quartoFamilia.setListaDeCamas(Arrays.asList());

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();

            // Assert
            assertEquals(0, capacidade, "Lista vazia deve retornar 0");
        }

        @Test
        @DisplayName("Deve retornar 0 para lista nula")
        void testCapacidadeMaximaListaNula() {
            // Arrange
            quartoFamilia.setListaDeCamas(null);

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();

            // Assert
            assertEquals(0, capacidade, "Lista nula deve retornar 0");
        }

        @Test
        @DisplayName("Deve calcular limite de hóspedes igual à capacidade máxima")
        void testCalcularLimiteHospedes() {
            // Arrange
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.SOLTEIRO
            )); // Capacidade = 3
            boolean solicitouBerco = false;

            // Act
            int limite = quartoFamilia.calcularLimiteHospedes(solicitouBerco);

            // Assert
            assertEquals(3, limite, "Limite deve ser igual à capacidade máxima");
        }
    }

    // ========================================================================
    // SEÇÃO 7: TESTES PARAMETRIZADOS E CASOS EXTREMOS
    // ========================================================================

    @DisplayName("Testes Parametrizados - Diferentes Valores Base")
    class TestesParametrizadosTest {

        private QuartoIndividual quartoIndividual;

        @BeforeEach
        void setUp() {
            quartoIndividual = new QuartoIndividual();
            quartoIndividual.setNumeroDeCamas(1);
            quartoIndividual.setAdicionalPorCama(30.0);
        }

        @ParameterizedTest
        @ValueSource(doubles = {50.0, 75.0, 100.0, 150.0, 200.0})
        @DisplayName("Deve calcular diária corretamente com diferentes valores base")
        void testDiariaComDiferentesValoresBase(double valorBase) {
            // Arrange
            quartoIndividual.setValorBase(valorBase);

            // Act
            double diaria = quartoIndividual.calcularDiaria(1, false);

            // Assert
            assertEquals(valorBase, diaria, "Diária deve ser igual ao valor base com 1 cama");
        }
    }

    // ========================================================================
    // SEÇÃO 8: TESTES DE INTEGRAÇÃO - CENÁRIOS COMPLETOS
    // ========================================================================

    @DisplayName("Cenários Completos - Integração entre métodos")
    class CenariosIntegracaoTest {

        @Test
        @DisplayName("Cenário: Quarto Duplo QUEEN com casal e bebê")
        void testCenarioQuartoDuploComBebe() {
            // Arrange
            QuartoDuplo quartoDuplo = new QuartoDuplo();
            quartoDuplo.setValorBase(150.0);
            quartoDuplo.setTipoCama(TipoCama.QUEEN);
            quartoDuplo.setPossuiBerco(false);

            int numeroDeHospedes = 3; // 2 adultos + 1 bebê
            boolean solicitouBerco = true;

            // Act - Verificar se é possível hospedar
            int limite = quartoDuplo.calcularLimiteHospedes(solicitouBerco);
            double diaria = quartoDuplo.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            assertTrue(numeroDeHospedes <= limite, "Número de hóspedes deve estar dentro do limite");
            assertEquals(3, limite, "Limite deve ser 3 com berço");
            assertEquals(215.0, diaria, "Diária deve ser 150 + 40 (QUEEN) + 25 (berço)");
        }

        @Test
        @DisplayName("Cenário: Quarto Família com múltiplas pessoas")
        void testCenarioQuartoFamiliaGrande() {
            // Arrange
            QuartoFamilia quartoFamilia = new QuartoFamilia();
            quartoFamilia.setValorBase(300.0);
            quartoFamilia.setListaDeCamas(Arrays.asList(
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.CASAL,
                TipoCamaFamilia.SOLTEIRO,
                TipoCamaFamilia.SOLTEIRO
            )); // Capacidade = 8

            int numeroDeHospedes = 8;
            boolean solicitouBerco = false;

            // Act
            int capacidade = quartoFamilia.getCapacidadeMaxima();
            int limite = quartoFamilia.calcularLimiteHospedes(solicitouBerco);
            double diaria = quartoFamilia.calcularDiaria(numeroDeHospedes, solicitouBerco);

            // Assert
            assertEquals(8, capacidade, "Capacidade deve ser 8");
            assertEquals(8, limite, "Limite deve ser 8");
            assertTrue(numeroDeHospedes <= limite, "Número de hóspedes deve estar dentro do limite");
            // 300 * (1 + 8*0.08) * (1 - 0.15) = 300 * 1.64 * 0.85 = 418.2
            assertEquals(418.2, diaria, 0.01, "Diária deve respeitar o desconto de 15%");
        }

        @Test
        @DisplayName("Cenário: Quarto Individual - rejeição de berço")
        void testCenarioQuartoIndividualRejeitaBerco() {
            // Arrange
            QuartoIndividual quartoIndividual = new QuartoIndividual();
            quartoIndividual.setValorBase(80.0);
            quartoIndividual.setNumeroDeCamas(1);

            int numeroDeHospedes = 1;
            boolean solicitouBerco = true;

            // Act & Assert
            assertThrows(RecursoNaoPermitidoException.class,
                () -> quartoIndividual.calcularDiaria(numeroDeHospedes, solicitouBerco),
                "Deve rejeitar berço em quarto individual"
            );

            assertThrows(RecursoNaoPermitidoException.class,
                () -> quartoIndividual.calcularLimiteHospedes(solicitouBerco),
                "Deve rejeitar berço ao calcular limite"
            );
        }
    }
}
