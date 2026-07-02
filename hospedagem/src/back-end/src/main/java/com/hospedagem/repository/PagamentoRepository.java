package com.hospedagem.repository;

import com.hospedagem.model.Pagamento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByAluguelId(Long aluguelId);
}
