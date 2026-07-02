# Sistema de Hospedagem

## Execução

### Back-end

```bash
cd src/back-end
./mvnw spring-boot:run
```

No Windows PowerShell, use `./mvnw.cmd spring-boot:run`.

O MySQL 8 pode ser iniciado com `docker compose up -d`. A API usa `http://localhost:8080` e libera CORS para `http://localhost:5173`.

### Front-end

```bash
cd src/front-end
npm install
npm run dev
```

## Testes e relatórios

```bash
cd src/back-end
./mvnw test
./mvnw surefire-report:report
```

No Windows, substitua `./mvnw` por `./mvnw.cmd`. Os XML/TXT ficam em `target/surefire-reports`; o HTML fica em `target/reports/surefire.html`.

### Camadas de teste

- **Unidade** (model, factory, strategy, service com Mockito): cobrem as regras de negócio isoladamente — cálculo de diárias e tarifação, capacidade por tipo de quarto, conflito de período (via mock), cancelamento e confirmação de pagamento.
- **Repositório** (`repository/AluguelRepositoryTest`, `@DataJpaTest` + perfil `h2`): valida a query `existeConflitoDePeriodo`/`existeConflitoDePeriodoExcluindo` contra o banco real — sobreposição no início, no fim, período contido, período adjacente (sem conflito), aluguel cancelado (não bloqueia) e exclusão do próprio aluguel ao atualizar.
- **Integração ponta a ponta** (`integration/AluguelFluxoIntegrationTest`, `@SpringBootTest` + `MockMvc` + perfil `h2`): sobe o contexto Spring completo e bate na API REST real, sem mocks — cadastra residência → quarto → cliente, cria o aluguel conferindo o cálculo real de diárias e tarifação, gera o recibo, tenta um overbooking (409), cancela, tenta cancelar de novo (400), confirma o pagamento, e cobre o bloqueio de cancelamento após pagamento confirmado (409) e os 404/400 de entidades inexistentes ou capacidade excedida.

O front é validado com:

```bash
npm run lint
npm run build
```

## Breaking changes de data e berço

- `dataEntrada` e `dataSaida` agora usam ISO local com horário, por exemplo `2026-07-10T14:00:00`.
- O cadastro de quarto duplo usa `possuiBerco`; `solicitouBerco` permanece somente no aluguel.

## Migração do banco de desenvolvimento

Como o projeto usa `ddl-auto=update` sem ferramenta de migração, a opção recomendada em desenvolvimento é recriar o volume antes de testar a nova versão:

```bash
docker compose down -v
docker compose up -d
```

Para preservar dados, faça backup e aplique manualmente no MySQL:

```sql
ALTER TABLE alugueis
  MODIFY COLUMN data_entrada DATETIME(6),
  MODIFY COLUMN data_saida DATETIME(6),
  ADD COLUMN numero_de_diarias INT NOT NULL DEFAULT 1;

CREATE TABLE pagamentos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  aluguel_id BIGINT NOT NULL UNIQUE,
  valor DOUBLE,
  status VARCHAR(255),
  data_confirmacao DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_pagamento_aluguel
    FOREIGN KEY (aluguel_id) REFERENCES alugueis(id)
);
```

Depois de validar os dados, remova o `DEFAULT 1` caso a política local exija que toda diária seja sempre preenchida pela aplicação.
