package com.hospedagem.service;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.dto.ReciboDTO;
import com.hospedagem.exception.CapacidadeExcedidaException;
import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.exception.NumeroHospedesInvalidoException;
import com.hospedagem.exception.QuartoIndisponivelException;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.Cliente;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.StatusAluguel;
import com.hospedagem.model.Pagamento;
import com.hospedagem.model.StatusPagamento;
import com.hospedagem.notificacao.AluguelCanceladoEvent;
import com.hospedagem.notificacao.AluguelCriadoEvent;
import com.hospedagem.notificacao.CentralNotificacoes;
import com.hospedagem.repository.AluguelRepository;
import com.hospedagem.repository.ClienteRepository;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.PagamentoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final QuartoRepository quartoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final ResidenciaRepository residenciaRepository;
    private final CalculadoraDiarias calculadoraDiarias;
    private final ServicoTarifacao servicoTarifacao;
    private final CentralNotificacoes centralNotificacoes;
    private final Clock relogio;

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          QuartoRepository quartoRepository,
                          PagamentoRepository pagamentoRepository,
                          ResidenciaRepository residenciaRepository,
                          CalculadoraDiarias calculadoraDiarias,
                          ServicoTarifacao servicoTarifacao,
                          CentralNotificacoes centralNotificacoes,
                          Clock relogio) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.quartoRepository = quartoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.residenciaRepository = residenciaRepository;
        this.calculadoraDiarias = calculadoraDiarias;
        this.servicoTarifacao = servicoTarifacao;
        this.centralNotificacoes = centralNotificacoes;
        this.relogio = relogio;
    }

    public List<Aluguel> listar() {
        return aluguelRepository.findAll();
    }

    public Aluguel buscarPorId(Long id) {
        return aluguelRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Aluguel nao encontrado para o id: " + id));
    }

    @Transactional
    public Aluguel criar(AluguelDTO dto) {
        Aluguel aluguel = new Aluguel();
        aluguel.setStatus(StatusAluguel.ATIVO);
        preencherECalcular(aluguel, dto, null);
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);

        Pagamento pagamento = new Pagamento();
        pagamento.setAluguel(aluguelSalvo);
        pagamento.setValor(aluguelSalvo.getValorTotal());
        pagamento.setStatus(StatusPagamento.PENDENTE);
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        aluguelSalvo.setPagamento(pagamentoSalvo);
        centralNotificacoes.publicar(new AluguelCriadoEvent(aluguelSalvo));

        return aluguelSalvo;
    }

    public Aluguel atualizar(Long id, AluguelDTO dto) {
        Aluguel aluguel = buscarPorId(id);
        preencherECalcular(aluguel, dto, id);
        return aluguelRepository.save(aluguel);
    }

    public Aluguel cancelar(Long id) {
        Aluguel aluguel = buscarPorId(id);

        aluguel.cancelar();
        Aluguel aluguelSalvo = aluguelRepository.save(aluguel);
        centralNotificacoes.publicar(new AluguelCanceladoEvent(aluguelSalvo));
        return aluguelSalvo;
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

    public List<Aluguel> listarPorResidencia(Long residenciaId) {
        residenciaRepository.findById(residenciaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Residência não encontrada para o id: " + residenciaId
            ));
        return aluguelRepository.findByQuartoResidenciaId(residenciaId);
    }

    public ReciboDTO gerarRecibo(Long id) {
        Aluguel aluguel = buscarPorId(id);
        Pagamento pagamento = pagamentoRepository.findByAluguelId(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Pagamento não encontrado para o aluguel: " + id
            ));

        return new ReciboDTO(
            aluguel.getDataEntrada(),
            aluguel.getDataSaida(),
            aluguel.getNumeroDeDiarias(),
            pagamento.getValor()
        );
    }

    private void preencherECalcular(Aluguel aluguel, AluguelDTO dto, Long idIgnorarConflito) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente nao encontrado para o id: " + dto.getClienteId()));

        Quarto quarto = quartoRepository.findById(dto.getQuartoId())
            .orElseThrow(() -> new EntityNotFoundException("Quarto nao encontrado para o id: " + dto.getQuartoId()));

        validarDatas(dto.getDataEntrada(), dto.getDataSaida());
        validarDisponibilidade(quarto, dto, idIgnorarConflito);
        validarHospedes(quarto, dto);

        int diarias = calculadoraDiarias.calcular(dto.getDataEntrada(), dto.getDataSaida());
        double valorDiaria = servicoTarifacao.calcular(
            quarto,
            dto.getNumeroDeHospedes(),
            dto.isSolicitouBerco(),
            dto.getDataEntrada(),
            dto.getDataSaida(),
            cliente
        );

        aluguel.setCliente(cliente);
        aluguel.setQuarto(quarto);
        aluguel.setDataEntrada(dto.getDataEntrada());
        aluguel.setDataSaida(dto.getDataSaida());
        aluguel.setNumeroDeDiarias(diarias);
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
            throw new NumeroHospedesInvalidoException();
        }

        int limite = quarto.calcularLimiteHospedes(dto.isSolicitouBerco());
        if (dto.getNumeroDeHospedes() > limite) {
            throw new CapacidadeExcedidaException(limite, dto.getNumeroDeHospedes());
        }
    }

    private void validarDatas(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        if (dataEntrada == null) {
            throw new DataInvalidaException("Data de entrada não pode ser nula.");
        }
        if (dataSaida == null) {
            throw new DataInvalidaException("Data de saída não pode ser nula.");
        }
        if (dataEntrada.toLocalDate().isBefore(LocalDate.now(relogio))) {
            throw new DataInvalidaException("Data de entrada deve ser hoje ou uma data futura.");
        }
        if (!dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException(dataEntrada, dataSaida);
        }
    }
}
