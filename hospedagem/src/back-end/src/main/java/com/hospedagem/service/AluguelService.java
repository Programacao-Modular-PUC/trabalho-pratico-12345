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
            .orElseThrow(() -> new EntityNotFoundException("Aluguel não encontrado: " + id));
    }

    public Aluguel criar(AluguelDTO dto) {
        if (!dto.getDataCheckOut().isAfter(dto.getDataCheckIn())) {
            throw new NegocioException("A data de check-out deve ser posterior ao check-in.");
        }

        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());
        Quarto quarto = quartoService.buscarPorId(dto.getQuartoId());

        long dias = ChronoUnit.DAYS.between(dto.getDataCheckIn(), dto.getDataCheckOut());
        double diaria = quarto.calcularDiaria(dto.getNumeroPessoas(), dto.isSolicitouBerco());

        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataCheckIn(dto.getDataCheckIn());
        aluguel.setDataCheckOut(dto.getDataCheckOut());
        aluguel.setNumeroPessoas(dto.getNumeroPessoas());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(diaria * dias);

        return repository.save(aluguel);
    }

    public Aluguel atualizar(Long id, AluguelDTO dto) {
        if (!dto.getDataCheckOut().isAfter(dto.getDataCheckIn())) {
            throw new NegocioException("A data de check-out deve ser posterior ao check-in.");
        }

        Aluguel aluguel = buscarPorId(id);
        Cliente cliente = clienteService.buscarPorId(dto.getClienteId());
        Quarto quarto = quartoService.buscarPorId(dto.getQuartoId());

        long dias = ChronoUnit.DAYS.between(dto.getDataCheckIn(), dto.getDataCheckOut());
        double diaria = quarto.calcularDiaria(dto.getNumeroPessoas(), dto.isSolicitouBerco());

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataCheckIn(dto.getDataCheckIn());
        aluguel.setDataCheckOut(dto.getDataCheckOut());
        aluguel.setNumeroPessoas(dto.getNumeroPessoas());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(diaria * dias);

        return repository.save(aluguel);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
