package com.hospedagem.service;

import com.hospedagem.model.Cliente;
import com.hospedagem.model.Quarto;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.tarifacao.ContextoTarifacao;
import com.hospedagem.tarifacao.PoliticaTarifacao;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServicoTarifacao {

    private final List<PoliticaTarifacao> politicas;
    private final AluguelRepository aluguelRepository;

    public ServicoTarifacao(List<PoliticaTarifacao> politicas,
                            AluguelRepository aluguelRepository) {
        this.politicas = politicas;
        this.aluguelRepository = aluguelRepository;
    }

    public double calcular(Quarto quarto,
                           int numeroDeHospedes,
                           boolean solicitouBerco,
                           LocalDateTime dataEntrada,
                           LocalDateTime dataSaida,
                           Cliente cliente) {
        double valorPorTipo = quarto.calcularDiaria(numeroDeHospedes, solicitouBerco);
        long quantidadeAlugueis = cliente.getId() == null
            ? 0
            : aluguelRepository.countByClienteId(cliente.getId());
        ContextoTarifacao contexto = new ContextoTarifacao(
            dataEntrada, dataSaida, cliente, quantidadeAlugueis
        );
        return aplicarPoliticas(valorPorTipo, contexto);
    }

    public double aplicarPoliticas(double valorDiaria, ContextoTarifacao contexto) {
        double valorFinal = valorDiaria;
        for (PoliticaTarifacao politica : politicas) {
            valorFinal = politica.aplicar(valorFinal, contexto);
        }
        return valorFinal;
    }
}
