package com.hospedagem.service;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.exception.NegocioException;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.QuartoFamilia;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.Residencia;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class QuartoService {

    private final QuartoRepository repository;
    private final ResidenciaRepository residenciaRepository;

    public QuartoService(QuartoRepository repository, ResidenciaRepository residenciaRepository) {
        this.repository = repository;
        this.residenciaRepository = residenciaRepository;
    }

    public List<Quarto> listar() {
        return repository.findAll();
    }

    public List<Quarto> listarPorTipo(String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);

        return repository.findAll().stream()
            .filter(quarto -> quarto.getTipo().equals(tipoNormalizado))
            .toList();
    }

    public Quarto buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quarto nao encontrado: " + id));
    }

    public Quarto criar(QuartoDTO dto) {
        Quarto quarto = construirQuarto(dto);
        return repository.save(quarto);
    }

    public Quarto atualizar(Long id, QuartoDTO dto) {
        Quarto quarto = buscarPorId(id);

        if (!quarto.getTipo().equalsIgnoreCase(dto.getTipo())) {
            throw new NegocioException("Nao e possivel alterar o tipo do quarto.");
        }

        preencherCamposComuns(quarto, dto);
        preencherCamposEspecificos(quarto, dto);

        return repository.save(quarto);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private Quarto construirQuarto(QuartoDTO dto) {
        String tipo = normalizarTipo(dto.getTipo());

        return switch (tipo) {
            case "INDIVIDUAL" -> criarIndividual(dto);
            case "DUPLO" -> criarDuplo(dto);
            case "FAMILIA" -> criarFamilia(dto);
            default -> throw new NegocioException("Tipo de quarto invalido. Use INDIVIDUAL, DUPLO ou FAMILIA.");
        };
    }

    private QuartoIndividual criarIndividual(QuartoDTO dto) {
        QuartoIndividual quarto = new QuartoIndividual();
        preencherCamposComuns(quarto, dto);
        preencherIndividual(quarto, dto);
        return quarto;
    }

    private QuartoDuplo criarDuplo(QuartoDTO dto) {
        QuartoDuplo quarto = new QuartoDuplo();
        preencherCamposComuns(quarto, dto);
        preencherDuplo(quarto, dto);
        return quarto;
    }

    private QuartoFamilia criarFamilia(QuartoDTO dto) {
        QuartoFamilia quarto = new QuartoFamilia();
        preencherCamposComuns(quarto, dto);
        preencherFamilia(quarto, dto);
        return quarto;
    }

    private void preencherCamposComuns(Quarto quarto, QuartoDTO dto) {
        // @NotNull no DTO já garante que valorBase não é null aqui; só checamos valor negativo
        if (dto.getValorBase() < 0) {
            throw new NegocioException("Valor base deve ser maior ou igual a zero.");
        }

        Residencia residencia = residenciaRepository.findById(dto.getResidenciaId())
            .orElseThrow(() -> new EntityNotFoundException("Residencia nao encontrada: " + dto.getResidenciaId()));

        quarto.setValorBase(dto.getValorBase());
        quarto.setPossuiAR(dto.isPossuiAR());
        quarto.setPossuiHidro(dto.isPossuiHidro());
        quarto.setResidencia(residencia);
    }

    private void preencherCamposEspecificos(Quarto quarto, QuartoDTO dto) {
        if (quarto instanceof QuartoIndividual individual) {
            preencherIndividual(individual, dto);
        } else if (quarto instanceof QuartoDuplo duplo) {
            preencherDuplo(duplo, dto);
        } else if (quarto instanceof QuartoFamilia familia) {
            preencherFamilia(familia, dto);
        }
    }

    private void preencherIndividual(QuartoIndividual quarto, QuartoDTO dto) {
        if (dto.getNumeroDeCamas() == null || dto.getNumeroDeCamas() < 1) {
            throw new NegocioException("Quarto individual precisa ter pelo menos 1 cama.");
        }

        if (dto.getAdicionalPorCama() != null && dto.getAdicionalPorCama() < 0) {
            throw new NegocioException("Adicional por cama nao pode ser negativo.");
        }

        quarto.setNumeroDeCamas(dto.getNumeroDeCamas());
        quarto.setAdicionalPorCama(dto.getAdicionalPorCama() == null ? 30.0 : dto.getAdicionalPorCama());
    }

    private void preencherDuplo(QuartoDuplo quarto, QuartoDTO dto) {
        if (dto.getTipoCama() == null) {
            throw new NegocioException("Quarto duplo precisa informar tipoCama: CASAL, QUEEN ou KING.");
        }

        quarto.setTipoCama(dto.getTipoCama());
        quarto.setPossuiBerco(dto.getSolicitouBerco() != null && dto.getSolicitouBerco());
    }

    private void preencherFamilia(QuartoFamilia quarto, QuartoDTO dto) {
        if (dto.getListaDeCamas() == null || dto.getListaDeCamas().isEmpty()) {
            throw new NegocioException("Quarto familia precisa ter uma listaDeCamas.");
        }

        if (dto.getQuantidadeDeAmbientes() == null || dto.getQuantidadeDeAmbientes() < 1) {
            throw new NegocioException("Quarto familia precisa ter pelo menos 1 ambiente.");
        }

        quarto.setListaDeCamas(dto.getListaDeCamas());
        quarto.setQuantidadeDeAmbientes(dto.getQuantidadeDeAmbientes());
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new NegocioException("Tipo de quarto deve ser informado.");
        }

        String tipoNormalizado = tipo.trim().toUpperCase(Locale.ROOT);

        if (!tipoNormalizado.equals("INDIVIDUAL")
            && !tipoNormalizado.equals("DUPLO")
            && !tipoNormalizado.equals("FAMILIA")) {
            throw new NegocioException("Tipo de quarto invalido. Use INDIVIDUAL, DUPLO ou FAMILIA.");
        }

        return tipoNormalizado;
    }
}

/*
 * O QUE MUDOU:
 * - preencherDuplo: `setSolicitouBerco` → `setPossuiBerco` para acompanhar o rename no model.
 * - preencherCamposComuns: removida verificação `valorBase == null` — o campo é @NotNull no DTO e já é barrado pelo Bean Validation antes de chegar ao service.
 */
