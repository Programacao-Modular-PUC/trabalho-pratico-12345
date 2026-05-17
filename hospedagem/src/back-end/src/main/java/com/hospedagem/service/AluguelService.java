package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.exception.NegocioException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.Quarto;
import com.hospedagem.repository.AluguelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository repository;
    private final ClienteService clienteService;
    private final QuartoService quartoService;

    public AluguelService(AluguelRepository repository, ClienteService clienteService, QuartoService quartoService) {
        this.repository = repository;
        this.clienteService = clienteService;
        this.quartoService = quartoService;
    }

    public List<Aluguel> listar() {
        return repository.findAll();
    }

    public Aluguel buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel nao encontrado: " + id));
    }

    public Aluguel criar(AluguelDTO dto) {
        Aluguel aluguel = new Aluguel();
        preencherECalcular(aluguel, dto);
        return repository.save(aluguel);
    }

    public Aluguel atualizar(Long id, AluguelDTO dto) {
        Aluguel aluguel = buscarPorId(id);
        preencherECalcular(aluguel, dto);
        return repository.save(aluguel);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private void preencherECalcular(Aluguel aluguel, AluguelDTO dto) {
        if (!dto.getDataSaida().isAfter(dto.getDataEntrada())) {
            throw new NegocioException("A data de saida deve ser posterior a data de entrada.");
        }

        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());
        Quarto quarto = quartoService.buscarPorId(dto.getQuartoId());

        int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
        if (dto.getNumeroDeHospedes() > limite) {
            throw new NegocioException("Numero de hospedes excede o limite do quarto: " + limite);
        }

        long dias = ChronoUnit.DAYS.between(dto.getDataEntrada(), dto.getDataSaida());
        double diaria = quarto.calcularDiaria(dto.getNumeroDeHospedes(), dto.isSolicitouBerco());

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dto.getDataEntrada());
        aluguel.setDataSaida(dto.getDataSaida());
        aluguel.setNumeroDeHospedes(dto.getNumeroDeHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(diaria * dias);
    }
}
