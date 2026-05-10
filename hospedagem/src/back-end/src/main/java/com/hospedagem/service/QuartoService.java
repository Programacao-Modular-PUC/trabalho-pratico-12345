package com.hospedagem.service;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.exception.NegocioException;
import com.hospedagem.model.*;
import com.hospedagem.repository.QuartoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository repository;

    public QuartoService(QuartoRepository repository) {
        this.repository = repository;
    }

    public List<Quarto> listar() {
        return repository.findAll();
    }

    public Quarto buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quarto não encontrado: " + id));
    }

    public Quarto criar(QuartoDTO dto) {
        Quarto quarto = construirQuarto(dto);
        return repository.save(quarto);
    }

    public Quarto atualizar(Long id, QuartoDTO dto) {
        Quarto existente = buscarPorId(id);
        String tipoExistente = existente.getClass().getSimpleName().replace("Quarto", "").toLowerCase();
        if (!tipoExistente.equals(dto.getTipo())) {
            throw new NegocioException("Não é possível alterar o tipo do quarto. Tipo atual: " + tipoExistente);
        }
        preencherCamposComuns(existente, dto);
        preencherCamposEspecificos(existente, dto);
        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private Quarto construirQuarto(QuartoDTO dto) {
        return switch (dto.getTipo().toLowerCase()) {
            case "individual" -> criarIndividual(dto);
            case "duplo"      -> criarDuplo(dto);
            case "familia"    -> criarFamilia(dto);
            default -> throw new NegocioException("Tipo de quarto inválido: " + dto.getTipo() + ". Use: individual, duplo ou familia.");
        };
    }

    private QuartoIndividual criarIndividual(QuartoDTO dto) {
        if (dto.getNumeroCamas() == null || dto.getNumeroCamas() < 1) {
            throw new NegocioException("Quarto individual requer numeroCamas >= 1.");
        }
        QuartoIndividual q = new QuartoIndividual();
        preencherCamposComuns(q, dto);
        q.setNumeroCamas(dto.getNumeroCamas());
        return q;
    }

    private QuartoDuplo criarDuplo(QuartoDTO dto) {
        if (dto.getTipoCama() == null) {
            throw new NegocioException("Quarto duplo requer tipoCama (CASAL, QUEEN ou KING).");
        }
        QuartoDuplo q = new QuartoDuplo();
        preencherCamposComuns(q, dto);
        q.setTipoCama(dto.getTipoCama());
        q.setPossuiBerco(dto.getPossuiBerco() != null && dto.getPossuiBerco());
        return q;
    }

    private QuartoFamilia criarFamilia(QuartoDTO dto) {
        QuartoFamilia q = new QuartoFamilia();
        preencherCamposComuns(q, dto);
        preencherCamposEspecificos(q, dto);
        return q;
    }

    private void preencherCamposComuns(Quarto quarto, QuartoDTO dto) {
        quarto.setValorBase(dto.getValorBase());
        quarto.setPossuiAr(dto.isPossuiAr());
        quarto.setPossuiHidro(dto.isPossuiHidro());
    }

    private void preencherCamposEspecificos(Quarto quarto, QuartoDTO dto) {
        if (quarto instanceof QuartoIndividual q) {
            if (dto.getNumeroCamas() != null) q.setNumeroCamas(dto.getNumeroCamas());
        } else if (quarto instanceof QuartoDuplo q) {
            if (dto.getTipoCama() != null) q.setTipoCama(dto.getTipoCama());
            if (dto.getPossuiBerco() != null) q.setPossuiBerco(dto.getPossuiBerco());
        } else if (quarto instanceof QuartoFamilia q) {
            if (dto.getCamasSolteiro() != null) q.setCamasSolteiro(dto.getCamasSolteiro());
            if (dto.getCamasCasal() != null) q.setCamasCasal(dto.getCamasCasal());
            if (dto.getCamasQueenKing() != null) q.setCamasQueenKing(dto.getCamasQueenKing());
            if (dto.getQuantidadeAmbientes() != null) q.setQuantidadeAmbientes(dto.getQuantidadeAmbientes());
        }
    }
}
