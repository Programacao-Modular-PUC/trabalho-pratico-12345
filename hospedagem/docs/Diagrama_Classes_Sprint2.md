# Diagrama de Classes - Sprint 2

```mermaid
classDiagram
    class Residencia {
        Long id
        String nome
        String endereco
        String descricao
        List~Quarto~ quartos
    }

    class Cliente {
        Long id
        String nome
        String cpf
        String email
        String telefone
    }

    class Aluguel {
        Long id
        LocalDate dataCheckIn
        LocalDate dataCheckOut
        int numeroPessoas
        boolean solicitouBerco
        Double valorTotal
    }

    class Quarto {
        <<abstract>>
        Long id
        Double valorBase
        boolean possuiAr
        boolean possuiHidro
        calcularDiaria(numeroPessoas, solicitouBerco) double
    }

    class QuartoIndividual {
        int numeroCamas
        calcularDiaria(numeroPessoas, solicitouBerco) double
    }

    class QuartoDuplo {
        TipoCama tipoCama
        boolean possuiBerco
        calcularDiaria(numeroPessoas, solicitouBerco) double
    }

    class QuartoFamilia {
        int camasSolteiro
        int camasCasal
        int camasQueenKing
        int quantidadeAmbientes
        calcularDiaria(numeroPessoas, solicitouBerco) double
    }

    class TipoCama {
        <<enumeration>>
        CASAL
        QUEEN
        KING
    }

    Residencia "1" --> "0..*" Quarto
    Cliente "1" --> "0..*" Aluguel
    Quarto "1" --> "0..*" Aluguel

    Quarto <-- QuartoIndividual
    Quarto <-- QuartoDuplo
    Quarto <-- QuartoFamilia

    QuartoDuplo --> TipoCama
