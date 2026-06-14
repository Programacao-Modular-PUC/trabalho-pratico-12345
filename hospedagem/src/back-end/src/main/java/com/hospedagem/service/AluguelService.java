package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.StatusAluguel;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.QuartoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final QuartoRepository quartoRepository;

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          QuartoRepository quartoRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.quartoRepository = quartoRepository;
    }

    public List<Aluguel> listar() {
        return aluguelRepository.findAll();
    }

    public Aluguel buscarPorId(Long id) {
        return aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel nao encontrado para o id: " + id));
    }

    public Aluguel criar(AluguelDTO dto) {
        Aluguel aluguel = new Aluguel();
        aluguel.setStatus(StatusAluguel.ATIVO);
        preencherECalcular(aluguel, dto, null);
        return aluguelRepository.save(aluguel);
    }

    public Aluguel atualizar(Long id, AluguelDTO dto) {
        Aluguel aluguel = buscarPorId(id);
        preencherECalcular(aluguel, dto, id);
        return aluguelRepository.save(aluguel);
    }

    public Aluguel cancelar(Long id) {
        Aluguel aluguel = buscarPorId(id);

        if (aluguel.getStatus() == StatusAluguel.CANCELADO) {
            throw new IllegalArgumentException("Aluguel ja esta cancelado.");
        }

        aluguel.setStatus(StatusAluguel.CANCELADO);
        return aluguelRepository.save(aluguel);
    }

    public void deletar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguelRepository.delete(aluguel);
    }

    public List<Aluguel> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
            .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado para o id: " + clienteId));
        return aluguelRepository.findByClienteId(clienteId);
    }

    private void preencherECalcular(Aluguel aluguel, AluguelDTO dto, Long idIgnorarConflito) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado para o id: " + dto.getClienteId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
            .orElseThrow(() -> new EntityNotFoundException("Quarto nao encontrado para o id: " + dto.getQuartoId()));

        validarDatas(dto.getDataEntrada(), dto.getDataSaida());
        validarDisponibilidade(quarto, dto, idIgnorarConflito);
        validarHospedes(quarto, dto);

        long diarias = ChronoUnit.DAYS.between(dto.getDataEntrada(), dto.getDataSaida());
        double valorDiaria = quarto.calcularDiaria(dto.getNumeroDeHospedes(), dto.isSolicitouBerco());

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dto.getDataEntrada());
        aluguel.setDataSaida(dto.getDataSaida());
        aluguel.setNumeroDeHospedes(dto.getNumeroDeHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(valorDiaria * diarias);
    }

    private void validarDisponibilidade(Quarto quarto, AluguelDTO dto, Long idIgnorarConflito) {
        boolean existeConflito;

        if (idIgnorarConflito == null) {
            existeConflito = aluguelRepository.existeConflitoDePeriodo(
                quarto.getId(), dto.getDataEntrada(), dto.getDataSaida());
        } else {
            existeConflito = aluguelRepository.existeConflitoDePeriodoExcluindo(
                quarto.getId(), dto.getDataEntrada(), dto.getDataSaida(), idIgnorarConflito);
        }

        if (existeConflito) {
            throw new QuartoIndisponivelException(quarto.getId(),
                "ja existe reserva entre " + dto.getDataEntrada() + " e " + dto.getDataSaida());
        }
    }

    private void validarHospedes(Quarto quarto, AluguelDTO dto) {
        if (dto.getNumeroDeHospedes() <= 0) {
            throw new IllegalArgumentException("Numero de hospedes deve ser maior que zero.");
        }

        int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
        if (dto.getNumeroDeHospedes() > limite) {
            throw new CapacidadeExcedidaException(limite, dto.getNumeroDeHospedes());
        }
    }

    private void validarDatas(LocalDate dataEntrada, LocalDate dataSaida) {
        if (dataEntrada == null) {
            throw new NullPointerException("Data de entrada nao pode ser nula.");
        }
        if (dataSaida == null) {
            throw new NullPointerException("Data de saida nao pode ser nula.");
        }
        if (dataEntrada.isBefore(LocalDate.now())) {
            throw new DataInvalidaException("Data de entrada deve ser hoje ou uma data futura.");
        }
        if (!dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException(dataEntrada, dataSaida);
        }
    }
}
