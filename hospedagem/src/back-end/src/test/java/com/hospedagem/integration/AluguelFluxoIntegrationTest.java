package com.hospedagem.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração ponta a ponta: sobe o contexto Spring completo
 * (Controller -> Service -> Factory/Strategy/Observer -> Repository -> JPA)
 * contra o banco H2 real (perfil "h2"), sem nenhum mock, exercitando a API
 * REST via HTTP simulado (MockMvc).
 *
 * Cobre o ciclo de vida completo de um aluguel: cadastro de residência,
 * quarto e cliente; criação do aluguel com cálculo real de diárias (RN de
 * check-in/check-out às 12h) e tarifação (valor base + adicionais + AR);
 * geração de recibo; bloqueio de overbooking (409); cancelamento; e o
 * bloqueio de cancelamento após confirmação de pagamento.
 *
 * @Transactional garante rollback ao final de cada teste, mantendo o H2 limpo
 * entre execuções.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class AluguelFluxoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // Usa sempre o ano seguinte para nunca cair em data passada (RN: aluguel exige data futura),
    // e março para ficar fora dos meses de alta/baixa temporada e de qualquer feriado fixo,
    // deixando o valor da diária previsível no teste.
    private final int anoFuturo = LocalDate.now().getYear() + 1;

    @Test
    void fluxoCompletoDeAluguel_criacaoConflitoCancelamentoEPagamento() throws Exception {
        // 1) Cadastrar residência
        String residenciaJson = """
                {
                  "nome": "Pousada Maracangalha",
                  "endereco": "Rua das Piscinas Naturais, 123, Maraú-BA",
                  "descricao": "Pousada à beira-mar"
                }
                """;

        String residenciaResponse = mockMvc.perform(post("/residencias")
                        .contentType("application/json")
                        .content(residenciaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Long residenciaId = objectMapper.readTree(residenciaResponse).get("id").asLong();

        // 2) Cadastrar quarto individual com 2 camas e ar-condicionado
        //    valorBase(200) + adicionalPorCama(30) * (2-1) + adicionalAR(40) = 270 / diária
        String quartoJson = """
                {
                  "tipo": "INDIVIDUAL",
                  "valorBase": 200.0,
                  "possuiAR": true,
                  "possuiHidro": false,
                  "residenciaId": %d,
                  "numeroDeCamas": 2
                }
                """.formatted(residenciaId);

        String quartoResponse = mockMvc.perform(post("/quartos")
                        .contentType("application/json")
                        .content(quartoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("INDIVIDUAL"))
                .andReturn().getResponse().getContentAsString();
        Long quartoId = objectMapper.readTree(quartoResponse).get("id").asLong();

        // 3) Cadastrar cliente
        String clienteJson = """
                {
                  "nome": "Joana da Silva",
                  "cpf": "12345678900",
                  "email": "joana@example.com",
                  "telefone": "73999990000"
                }
                """;

        String clienteResponse = mockMvc.perform(post("/clientes")
                        .contentType("application/json")
                        .content(clienteJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long clienteId = objectMapper.readTree(clienteResponse).get("id").asLong();

        // 4) Criar aluguel: 20/03 14h (após 12h, sem desconto) -> 22/03 10h (antes de 12h, sem diária extra)
        //    = 2 diárias * 270 = 540,00 (nenhuma política de tarifação sazonal se aplica em março)
        String aluguelJson = """
                {
                  "clienteId": %d,
                  "quartoId": %d,
                  "dataEntrada": "%d-03-20T14:00:00",
                  "dataSaida": "%d-03-22T10:00:00",
                  "numeroDeHospedes": 2,
                  "solicitouBerco": false
                }
                """.formatted(clienteId, quartoId, anoFuturo, anoFuturo);

        String aluguelResponse = mockMvc.perform(post("/alugueis")
                        .contentType("application/json")
                        .content(aluguelJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroDeDiarias").value(2))
                .andExpect(jsonPath("$.valorTotal").value(540.0))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.pagamento.status").value("PENDENTE"))
                .andReturn().getResponse().getContentAsString();

        JsonNode aluguelNode = objectMapper.readTree(aluguelResponse);
        Long aluguelId = aluguelNode.get("id").asLong();
        Long pagamentoId = aluguelNode.get("pagamento").get("id").asLong();

        // 5) Conferir recibo
        mockMvc.perform(get("/alugueis/{id}/recibo", aluguelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroDeDiarias").value(2))
                .andExpect(jsonPath("$.totalAPagar").value(540.0));

        // 6) Tentar alugar o MESMO quarto em período sobreposto -> 409 (overbooking)
        String aluguelConflitanteJson = """
                {
                  "clienteId": %d,
                  "quartoId": %d,
                  "dataEntrada": "%d-03-21T12:00:00",
                  "dataSaida": "%d-03-25T12:00:00",
                  "numeroDeHospedes": 1,
                  "solicitouBerco": false
                }
                """.formatted(clienteId, quartoId, anoFuturo, anoFuturo);

        mockMvc.perform(post("/alugueis")
                        .contentType("application/json")
                        .content(aluguelConflitanteJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").isNotEmpty());

        // 7) Cancelar o aluguel (pagamento ainda pendente -> permitido)
        mockMvc.perform(patch("/alugueis/{id}/cancelar", aluguelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        // 8) Cancelar de novo -> 400 (já cancelado)
        mockMvc.perform(patch("/alugueis/{id}/cancelar", aluguelId))
                .andExpect(status().isBadRequest());

        // 9) Confirmar o pagamento do aluguel já cancelado
        mockMvc.perform(patch("/pagamentos/{id}/confirmar", pagamentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    void criarAluguelComPagamentoConfirmado_naoDevePermitirCancelamento() throws Exception {
        Long residenciaId = criarResidencia();
        Long quartoId = criarQuartoIndividual(residenciaId);
        Long clienteId = criarCliente("98765432100");

        String aluguelJson = """
                {
                  "clienteId": %d,
                  "quartoId": %d,
                  "dataEntrada": "%d-03-10T14:00:00",
                  "dataSaida": "%d-03-11T10:00:00",
                  "numeroDeHospedes": 1,
                  "solicitouBerco": false
                }
                """.formatted(clienteId, quartoId, anoFuturo, anoFuturo);

        String aluguelResponse = mockMvc.perform(post("/alugueis")
                        .contentType("application/json")
                        .content(aluguelJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode aluguelNode = objectMapper.readTree(aluguelResponse);
        Long aluguelId = aluguelNode.get("id").asLong();
        Long pagamentoId = aluguelNode.get("pagamento").get("id").asLong();

        mockMvc.perform(patch("/pagamentos/{id}/confirmar", pagamentoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));

        // Pagamento confirmado deve bloquear o cancelamento (409)
        mockMvc.perform(patch("/alugueis/{id}/cancelar", aluguelId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").isNotEmpty());
    }

    @Test
    void criarAluguelComHospedesAcimaDaCapacidade_deveRetornar400() throws Exception {
        Long residenciaId = criarResidencia();
        Long quartoId = criarQuartoIndividual(residenciaId); // limite = numeroDeCamas = 2
        Long clienteId = criarCliente("11122233344");

        String aluguelJson = """
                {
                  "clienteId": %d,
                  "quartoId": %d,
                  "dataEntrada": "%d-03-05T14:00:00",
                  "dataSaida": "%d-03-06T10:00:00",
                  "numeroDeHospedes": 5,
                  "solicitouBerco": false
                }
                """.formatted(clienteId, quartoId, anoFuturo, anoFuturo);

        mockMvc.perform(post("/alugueis")
                        .contentType("application/json")
                        .content(aluguelJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarAluguelInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(get("/alugueis/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").isNotEmpty());
    }

    private Long criarResidencia() throws Exception {
        String json = """
                {"nome": "Residência Teste", "endereco": "Rua Teste, 1", "descricao": "desc"}
                """;
        String response = mockMvc.perform(post("/residencias")
                        .contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long criarQuartoIndividual(Long residenciaId) throws Exception {
        String json = """
                {
                  "tipo": "INDIVIDUAL",
                  "valorBase": 150.0,
                  "possuiAR": false,
                  "possuiHidro": false,
                  "residenciaId": %d,
                  "numeroDeCamas": 2
                }
                """.formatted(residenciaId);
        String response = mockMvc.perform(post("/quartos")
                        .contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long criarCliente(String cpf) throws Exception {
        String json = """
                {"nome": "Cliente Teste", "cpf": "%s", "email": "teste@example.com", "telefone": "31988887777"}
                """.formatted(cpf);
        String response = mockMvc.perform(post("/clientes")
                        .contentType("application/json").content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}
