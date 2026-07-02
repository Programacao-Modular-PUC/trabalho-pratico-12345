# 📋 Guia de Testes JUnit - Sistema de Hospedagem

## 📌 Visão Geral

Este documento apresenta a cobertura de testes unitários com **JUnit 5** para a parte do projeto: **"Cálculo de Diária, Regras de Berço, Limites de Hóspedes e Disponibilidade"**.

---

## 🎯 Estrutura dos Testes

### **Arquivo 1: QuartoServiceTest.java**
Testa a lógica de negócio dos modelos de quartos:

#### 1️⃣ **QuartoIndividual - Cálculo de Diária**
- ✅ Diária com 1 cama = valorBase
- ✅ Diária com 2 camas = valorBase + (30 * 1)
- ✅ Diária com 3 camas = valorBase + (30 * 2)
- ✅ Respeita adicional customizado por cama

**Lógica testada:**
```java
double diaria = getValorBase() + (adicionalPorCama * (numeroDeCamas - 1));
```

#### 2️⃣ **QuartoIndividual - Regras de Berço**
- ✅ Lança `RecursoNaoPermitidoException` ao solicitar berço
- ✅ Exceção contém mensagem clara sobre restrição
- ✅ Calcula limite corretamente sem berço

**Lógica testada:**
```java
if (solicitouBerco) {
    throw new RecursoNaoPermitidoException(
        "berço", "Quarto Individual", 
        "quartos individuais não acomodam berço"
    );
}
```

#### 3️⃣ **QuartoDuplo - Cálculo de Diária**
- ✅ CASAL sem berço = 150 (base) + 0 = 150
- ✅ QUEEN sem berço = 150 + 40 = 190
- ✅ KING sem berço = 150 + 60 = 210
- ✅ Com berço de fábrica = adiciona taxa de 25
- ✅ Com berço solicitado = adiciona taxa de 25
- ✅ Com ambos os berços = usa lógica OR (adiciona uma vez)

**Lógica testada:**
```java
double adicionalTipoCama = switch (tipoCama) {
    case CASAL -> 0.0;
    case QUEEN -> 40.0;
    case KING -> 60.0;
};
boolean usarBerco = possuiBerco || solicitouBercoNoAluguel;
double taxaBerco = usarBerco ? 25.0 : 0.0;
return getValorBase() + adicionalTipoCama + taxaBerco;
```

#### 4️⃣ **QuartoDuplo - Limites de Hóspedes**
- ✅ Sem berço = 2 hóspedes
- ✅ Com berço de fábrica = 3 hóspedes
- ✅ Com berço solicitado = 3 hóspedes
- ✅ Com ambos os berços = 3 hóspedes (não duplica)

#### 5️⃣ **QuartoFamilia - Cálculo de Diária com Desconto**
- ✅ 1 hóspede (0% desconto) = 200 * 1.08 = 216
- ✅ 3 hóspedes (0% desconto) = 200 * 1.24 = 248
- ✅ 4 hóspedes (5% desconto) = 200 * 1.32 * 0.95 = 250.8
- ✅ 5 hóspedes (5% desconto) = 200 * 1.40 * 0.95 = 266
- ✅ 6 hóspedes (10% desconto) = 200 * 1.48 * 0.90 = 266.4
- ✅ 7 hóspedes (10% desconto) = 200 * 1.56 * 0.90 = 280.8
- ✅ 8 hóspedes (15% desconto) = 200 * 1.64 * 0.85 = 278.8
- ✅ 10 hóspedes (15% desconto) = 200 * 1.80 * 0.85 = 306

**Lógica testada:**
```java
double valor = getValorBase() * (1 + (numeroDeHospedes * 0.08));
double desconto = calcularDesconto(numeroDeHospedes);
return valor * (1 - desconto);

// Desconto:
// >= 8: 15%
// >= 6: 10%
// >= 4: 5%
// < 4: 0%
```

#### 6️⃣ **QuartoFamilia - Limites e Capacidade**
- ✅ CASAL = 2 pessoas por cama
- ✅ SOLTEIRO = 1 pessoa por cama
- ✅ Lista vazia = 0 pessoas
- ✅ Lista nula = 0 pessoas
- ✅ Limite = Capacidade máxima

#### 7️⃣ **Testes Parametrizados**
- ✅ Diferentes valores base (50, 75, 100, 150, 200)

#### 8️⃣ **Cenários Completos de Integração**
- ✅ Quarto Duplo com casal + bebê
- ✅ Quarto Família grande (8 pessoas)
- ✅ Quarto Individual rejeitando berço

---

### **Arquivo 2: AluguelServiceTest.java**
Testa a lógica de negócio do serviço de aluguel:

#### 1️⃣ **Disponibilidade do Quarto**
- ✅ Cria aluguel para quarto disponível
- ✅ Lança `QuartoIndisponivelException` para período conflitante
- ✅ Valida conflito ao atualizar aluguel
- ✅ Permite atualização ignorando o próprio aluguel

**Método validado:**
```java
if (aluguelRepository.existeConflitoDePeriodo(
    quarto.getId(), 
    dto.getDataEntrada(), 
    dto.getDataSaida()
)) {
    throw new QuartoIndisponivelException(...);
}
```

#### 2️⃣ **Validação de Datas**
- ✅ Rejeita data de entrada nula
- ✅ Rejeita data de saída nula
- ✅ Rejeita data de entrada no passado
- ✅ Rejeita data de entrada hoje
- ✅ Aceita data de entrada a partir de amanhã
- ✅ Rejeita data de saída igual à entrada
- ✅ Rejeita data de saída anterior à entrada
- ✅ Aceita períodos longos (semanas/meses)

**Método validado:**
```java
private void validarDatas(LocalDate dataEntrada, LocalDate dataSaida) {
    if (dataEntrada == null || dataSaida == null) {
        throw new NullPointerException(...);
    }
    if (!dataEntrada.isBefore(LocalDate.now())) {
        throw new DataInvalidaException("A data de entrada deve ser a partir de hoje.");
    }
    if (!dataSaida.isAfter(dataEntrada)) {
        throw new DataInvalidaException(dataEntrada, dataSaida);
    }
}
```

#### 3️⃣ **Número de Hóspedes**
- ✅ Rejeita 0 hóspedes
- ✅ Rejeita número negativo
- ✅ Aceita 1 hóspede

#### 4️⃣ **Capacidade do Quarto**
- ✅ Rejeita quando hóspedes excedem capacidade (sem berço)
- ✅ Rejeita quando hóspedes excedem capacidade (com berço)
- ✅ Aceita no limite exato
- ✅ Aceita com berço no limite

**Método validado:**
```java
int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
if (dto.getNumeroDeHospedes() > limite) {
    throw new CapacidadeExcedidaException(limite, dto.getNumeroDeHospedes());
}
```

#### 5️⃣ **Testes Parametrizados**
- ✅ Múltiplas quantidades válidas de hóspedes (1, 2, 5, 10, 20)

#### 6️⃣ **Cálculo do Valor Total**
- ✅ Calcula valor total = diária × número de noites
- ✅ Inclui berço no cálculo
- ✅ Respeta descontos (QuartoFamilia)

**Método validado:**
```java
long diarias = dto.getDataEntrada().until(dto.getDataSaida()).getDays();
double valorDiaria = quarto.calcularDiaria(
    dto.getNumeroDeHospedes(), 
    dto.isSolicitouBerco()
);
double valorTotal = valorDiaria * diarias;
```

---

## 📊 Cobertura de Testes

| Funcionalidade | QuartoServiceTest | AluguelServiceTest | Total |
|---|---|---|---|
| **Cálculo de Diária** | 8 testes | 2 testes | **10** |
| **Regras de Berço** | 6 testes | 0 testes | **6** |
| **Limites de Hóspedes** | 6 testes | 1 teste | **7** |
| **Disponibilidade** | 0 testes | 4 testes | **4** |
| **Validação de Datas** | 0 testes | 7 testes | **7** |
| **Validação de Hóspedes** | 0 testes | 3 testes | **3** |
| **Capacidade do Quarto** | 0 testes | 4 testes | **4** |
| **Testes Parametrizados** | 1 teste | 1 teste | **2** |
| **Integração/Cenários** | 3 testes | 0 testes | **3** |
| **TOTAL** | **24 testes** | **22 testes** | **46 testes** |

---

## 🚀 Como Usar

### 1. **Copiar os arquivos para o projeto:**
```bash
# Copiar para src/test/java/com/hospedagem/service/
cp QuartoServiceTest.java [seu-projeto]/src/test/java/com/hospedagem/service/
cp AluguelServiceTest.java [seu-projeto]/src/test/java/com/hospedagem/service/
```

### 2. **Adicionar dependências ao pom.xml** (se necessário):
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.0</version>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.0.0</version>
    <scope>test</scope>
</dependency>
```

### 3. **Executar os testes:**
```bash
# Com Maven
mvn test

# Apenas os testes de Quarto
mvn test -Dtest=QuartoServiceTest

# Apenas os testes de Aluguel
mvn test -Dtest=AluguelServiceTest

# Com cobertura
mvn test jacoco:report
```

### 4. **Visualizar Resultados:**
```bash
# Relatório HTML (após executar com Jacoco)
target/site/jacoco/index.html
```

---

## 🔍 Exemplo de Teste

### QuartoServiceTest:
```java
@Test
@DisplayName("Deve calcular diária com QUEEN e berço solicitado")
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
```

### AluguelServiceTest:
```java
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
```

---

## 🎓 Conceitos Testados

### **JUnit 5 Features:**
- ✅ `@Test` - Marca método como teste
- ✅ `@DisplayName` - Nome descritivo dos testes
- ✅ `@BeforeEach` - Setup antes de cada teste
- ✅ `@ParameterizedTest` - Testes com múltiplos valores
- ✅ `@ValueSource` - Fonte de valores para testes parametrizados
- ✅ Nested classes com `@DisplayName`

### **Assertions:**
- ✅ `assertEquals()` - Comparação de valores
- ✅ `assertTrue()` / `assertFalse()` - Verificação booleana
- ✅ `assertThrows()` - Verifica se exceção é lançada
- ✅ `assertNotNull()` - Verifica se não é nulo

### **Mockito:**
- ✅ `@Mock` - Cria mock de dependência
- ✅ `when(...).thenReturn(...)` - Define comportamento
- ✅ `verify()` - Verifica chamadas

---

## 📝 Padrão de Testes (AAA)

Todos os testes seguem o padrão **Arrange-Act-Assert**:

1. **Arrange**: Preparar dados e configurações
2. **Act**: Executar a ação/método
3. **Assert**: Verificar resultado

---

## ✅ Checklist para Apresentação

- [ ] Todos os 46 testes passando
- [ ] Cobertura > 80% das linhas
- [ ] Documentação clara em cada teste
- [ ] Nenhuma dependência com banco de dados
- [ ] Testes executados em < 1 segundo
- [ ] Código limpo e bem organizado
- [ ] Exemplos de casos de sucesso e falha

---

## 🤝 Próximos Passos

1. **Adicionar testes de integração** (com banco H2)
2. **Adicionar testes de Controller** (endpoints REST)
3. **Aumentar cobertura** para 90%+
4. **Testes de performance** para grandes períodos

---

## 📞 Suporte

Se encontrar problemas:

1. Verifique se JUnit 5 está no classpath
2. Verifique a versão do Java (11+)
3. Execute `mvn clean test`
4. Veja os logs de erro detalhados

---

**Autor:** Sistema de Hospedagem - Trabalho Prático  
**Data:** 2026  
**Versão:** 1.0
