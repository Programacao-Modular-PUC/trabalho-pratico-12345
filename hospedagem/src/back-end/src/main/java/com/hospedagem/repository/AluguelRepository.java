package com.hospedagem.repository;

import com.hospedagem.model.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AluguelRepository extends JpaRepository<Aluguel, Long> {

    // Verifica se já existe aluguel para o quarto no período (usado para checar disponibilidade)
    @Query("""
        SELECT COUNT(a) > 0 FROM Aluguel a
        WHERE a.quarto.id = :quartoId
          AND a.dataEntrada < :dataSaida
          AND a.dataSaida > :dataEntrada
          AND (a.status IS NULL OR a.status <> com.hospedagem.model.StatusAluguel.CANCELADO)
    """)
    boolean existeConflitoDePeriodo(
        @Param("quartoId") Long quartoId,
        @Param("dataEntrada") LocalDate dataEntrada,
        @Param("dataSaida") LocalDate dataSaida
    );

    // Verifica conflito excluindo o próprio aluguel (usado no atualizar)
    @Query("""
        SELECT COUNT(a) > 0 FROM Aluguel a
        WHERE a.quarto.id = :quartoId
          AND a.id <> :idIgnorar
          AND a.dataEntrada < :dataSaida
          AND a.dataSaida > :dataEntrada
          AND (a.status IS NULL OR a.status <> com.hospedagem.model.StatusAluguel.CANCELADO)
    """)
    boolean existeConflitoDePeriodoExcluindo(
        @Param("quartoId") Long quartoId,
        @Param("dataEntrada") LocalDate dataEntrada,
        @Param("dataSaida") LocalDate dataSaida,
        @Param("idIgnorar") Long idIgnorar
    );

    // Histórico de alugueis por cliente
    List<Aluguel> findByClienteId(Long clienteId);

    // Filtro por tipo de quarto usando discriminator
    @Query("""
        SELECT a FROM Aluguel a
        WHERE TYPE(a.quarto) = :tipo
    """)
    List<Aluguel> findByTipoQuarto(@Param("tipo") Class<?> tipo);
}
