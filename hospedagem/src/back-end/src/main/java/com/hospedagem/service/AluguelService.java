package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.Quarto;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.QuartoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    // -------------------------------------------------------
    // Listar todos
    // -------------------------------------------------------

    public List<Aluguel> listar() {
        return aluguelRepository.findAll();
    }

    // -------------------------------------------------------
    // Buscar por ID
    // -------------------------------------------------------

    public Aluguel buscarPorId(Long id) {
        // NoSuchElementException do Java caso não exista
        return aluguelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado para o id: " + id));
    }

    // -------------------------------------------------------
    // Criar
    // -------------------------------------------------------

    public Aluguel criar(AluguelDTO dto) {
        // Busca entidades — IllegalArgumentException do Java se não existir
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Cliente não encontrado para o id: " + dto.getClienteId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Quarto não encontrado para o id: " + dto.getQuartoId()));

        // Valida datas (DataInvalidaException e NullPointerException do Java)
        validarDatas(dto.getDataEntrada(), dto.getDataSaida());

        // Valida disponibilidade do quarto no período (QuartoIndisponivelException)
        if (aluguelRepository.existeConflitoDePeriodo(quarto.getId(), dto.getDataEntrada(), dto.getDataSaida())) {
            throw new QuartoIndisponivelException(quarto.getId(),
                "já existe reserva entre " + dto.getDataEntrada() + " e " + dto.getDataSaida());
        }

        // Valida número de hóspedes (IllegalArgumentException do Java)
        if (dto.getNumeroDeHospedes() <= 0) {
            throw new IllegalArgumentException("Número de hóspedes deve ser maior que zero.");
        }

        // Valida capacidade e berço — calcularLimiteHospedes já lança
        // RecursoNaoPermitidoException se for QuartoIndividual com berço
        int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
        if (dto.getNumeroDeHospedes() > limite) {
            throw new CapacidadeExcedidaException(limite, dto.getNumeroDeHospedes());
        }

        // Calcula valor total
        long diarias = dto.getDataEntrada().until(dto.getDataSaida()).getDays();
        double valorDiaria = quarto.calcularDiaria(dto.getNumeroDeHospedes(), dto.isSolicitouBerco());
        double valorTotal = valorDiaria * diarias;

        // Monta e salva
        Aluguel aluguel = new Aluguel();
        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dto.getDataEntrada());
        aluguel.setDataSaida(dto.getDataSaida());
        aluguel.setNumeroDeHospedes(dto.getNumeroDeHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(valorTotal);

        return aluguelRepository.save(aluguel);
    }

    // -------------------------------------------------------
    // Atualizar
    // -------------------------------------------------------

    public Aluguel atualizar(Long id, AluguelDTO dto) {
        Aluguel aluguel = buscarPorId(id);

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Cliente não encontrado para o id: " + dto.getClienteId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
            .orElseThrow(() -> new IllegalArgumentException(
                "Quarto não encontrado para o id: " + dto.getQuartoId()));

        validarDatas(dto.getDataEntrada(), dto.getDataSaida());

        // Verifica conflito ignorando o próprio aluguel
        if (aluguelRepository.existeConflitoDePeriodoExcluindo(
                quarto.getId(), dto.getDataEntrada(), dto.getDataSaida(), id)) {
            throw new QuartoIndisponivelException(quarto.getId(),
                "já existe reserva entre " + dto.getDataEntrada() + " e " + dto.getDataSaida());
        }

        if (dto.getNumeroDeHospedes() <= 0) {
            throw new IllegalArgumentException("Número de hóspedes deve ser maior que zero.");
        }

        int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
        if (dto.getNumeroDeHospedes() > limite) {
            throw new CapacidadeExcedidaException(limite, dto.getNumeroDeHospedes());
        }

        long diarias = dto.getDataEntrada().until(dto.getDataSaida()).getDays();
        double valorDiaria = quarto.calcularDiaria(dto.getNumeroDeHospedes(), dto.isSolicitouBerco());

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dto.getDataEntrada());
        aluguel.setDataSaida(dto.getDataSaida());
        aluguel.setNumeroDeHospedes(dto.getNumeroDeHospedes());
        aluguel.setSolicitouBerco(dto.isSolicitouBerco());
        aluguel.setValorTotal(valorDiaria * diarias);

        return aluguelRepository.save(aluguel);
    }

    // -------------------------------------------------------
    // Deletar (cancelamento de aluguel)
    // -------------------------------------------------------

    public void deletar(Long id) {
        Aluguel aluguel = buscarPorId(id);
        aluguelRepository.delete(aluguel);
    }

    // -------------------------------------------------------
    // Histórico por cliente
    // -------------------------------------------------------

    public List<Aluguel> listarPorCliente(Long clienteId) {
        clienteRepository.findById(clienteId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Cliente não encontrado para o id: " + clienteId));
        return aluguelRepository.findByClienteId(clienteId);
    }

    // -------------------------------------------------------
    // Validação de datas (privado)
    // -------------------------------------------------------

    private void validarDatas(LocalDate dataEntrada, LocalDate dataSaida) {
        // NullPointerException do Java para datas nulas
        if (dataEntrada == null) {
            throw new NullPointerException("Data de entrada não pode ser nula.");
        }
        if (dataSaida == null) {
            throw new NullPointerException("Data de saída não pode ser nula.");
        }
        // DataInvalidaException para regras de negócio
        if (!dataEntrada.isBefore(LocalDate.now())) {
            throw new DataInvalidaException("A data de entrada deve ser a partir de hoje.");
        }
        if (!dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException(dataEntrada, dataSaida);
        }
    }
}
