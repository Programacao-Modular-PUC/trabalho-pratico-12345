# Relatório técnico — Sprint 4

## Funcionalidades escolhidas

Foram implementadas a Opção 1, tarifação flexível, e a Opção 3, central de notificações. Além delas, foi implementado o Singleton obrigatório para configuração global.

## Opção 1 — Tarifação flexível com Strategy

O cálculo específico do quarto continua polimórfico em `Quarto.calcularDiaria()`. O problema era aplicar regras contextuais — temporada, feriado e frequência do cliente — sem modificar cada subtipo.

`PoliticaTarifacao` define a Strategy. As implementações atuais são:

- `TarifacaoPadrao`: não altera o valor;
- `TarifacaoAltaTemporada`: acréscimo de 20%;
- `TarifacaoBaixaTemporada`: desconto de 10%;
- `TarifacaoFeriado`: acréscimo de 15%;
- `DescontoClienteFrequente`: desconto de 10% a partir de três aluguéis anteriores.

O Spring injeta todas as implementações em `ServicoTarifacao`, respeitando `@Order`. Para adicionar uma política, basta criar outra classe que implemente `PoliticaTarifacao` e registrá-la como componente; o serviço e as políticas existentes não mudam.

## Opção 3 — Notificações com Observer e Factory

`CentralNotificacoes` é o subject do Observer. Os services publicam `AluguelCriadoEvent`, `AluguelCanceladoEvent` e `PagamentoConfirmadoEvent`. Os observers são `CanalEmail`, `CanalSMS`, `CanalWhatsApp` e `CanalInterno`.

Os canais são simulados: registram a mensagem em memória e no log, sem integração externa. `FabricaCanalNotificacao` mantém um registro por `TipoCanalNotificacao`. Para adicionar um canal, cria-se a classe, registra-se o tipo e o bean passa a ser descoberto pela fábrica; os publishers permanecem inalterados.

## Singleton obrigatório

`ConfiguracaoGlobalHospedagem` usa o holder idiom: construtor privado, classe holder estática e `getInstance()`. A inicialização é lazy e thread-safe pela garantia de inicialização de classes da JVM.

A instância concentra somente configuração imutável: horário-base das 12h, timezone, valores de AR/hidro/berço e percentuais de temporada. Ela não é um bean Spring. O Spring continua responsável pelos services, repositories, Strategies, observers e fábrica; o Singleton GoF existe explicitamente para atender à necessidade de configuração global única.

O Singleton não é o único padrão: convive com Strategy, Observer e Factory.

## Benefícios

- Nova política tarifária: criar uma implementação de `PoliticaTarifacao`.
- Novo canal: criar um `CanalNotificacao` e registrá-lo na fábrica.
- Novo evento: criar uma implementação de `EventoHospedagem` e publicá-la no ponto de domínio.
- Alteração global de horário/adicionais: um único ponto em `ConfiguracaoGlobalHospedagem`.
- Regras de quarto continuam polimórficas e isoladas dos modificadores contextuais.

## Mapa feedback → correção

| Feedback | Correção aplicada |
|---|---|
| Exceções genéricas | `exception/`: classes específicas; `NegocioException` removida. |
| Hierarquia pouco específica | Exceções agora estendem `IllegalArgumentException`, `IllegalStateException` ou `UnsupportedOperationException`. |
| Falta de testes | `src/test`: testes unitários, MockMvc e H2. |
| Regra das 12h ausente | `CalculadoraDiarias` + `LocalDateTime` + `Clock`. |
| AR/hidro ignorados | `Quarto.calcularAdicionaisComuns()` lê o Singleton. |
| Berço ambíguo | `QuartoDuplo` separa oferta (`possuiBerco`) de solicitação do aluguel. |
| Pagamento/recibo ausentes | `Pagamento`, `PagamentoService`, `ReciboDTO` e endpoints. |
| Histórico por residência ausente | `findByQuartoResidenciaId()` e `/alugueis/residencia/{id}`. |
| Polimorfismo subutilizado | `getTipo()` abstrato e `QuartoFactory`. |
| Entidades expostas | `AluguelResponse`, `QuartoResponse`, `ClienteResponse`, `ResidenciaResponse`, `PagamentoResponse`. |
| Front com responsabilidades excessivas | Forms, tables, históricos, schemas, `StatusBadge` e `useConfirm` extraídos. |
| CORS ausente | `ConfiguracaoAplicacao.addCorsMappings()`. |

## Regra de cancelamento escolhida

Aluguel com pagamento confirmado não pode ser cancelado. A API responde conflito HTTP 409. Pagamentos pendentes permitem cancelamento, e o aluguel cancelado deixa de bloquear o período.
