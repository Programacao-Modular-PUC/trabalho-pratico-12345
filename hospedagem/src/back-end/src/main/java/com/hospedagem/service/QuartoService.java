package com.hospedagem.service;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.exception.AlteracaoTipoQuartoNaoPermitidaException;
import com.hospedagem.factory.QuartoFactory;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.Residencia;
import com.hospedagem.repository.QuartoRepository;
import com.hospedagem.repository.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QuartoService {

    private final QuartoRepository repository;
    private final ResidenciaRepository residenciaRepository;
    private final QuartoFactory factory;

    public QuartoService(QuartoRepository repository,
                         ResidenciaRepository residenciaRepository,
                         QuartoFactory factory) {
        this.repository = repository;
        this.residenciaRepository = residenciaRepository;
        this.factory = factory;
    }

    public List<Quarto> listar() {
        return repository.findAll();
    }

    public List<Quarto> listarPorTipo(String tipo) {
        String tipoNormalizado = factory.normalizarTipo(tipo);
        return repository.findAll().stream()
            .filter(quarto -> quarto.getTipo().equals(tipoNormalizado))
            .toList();
    }

    public Quarto buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Quarto não encontrado: " + id));
    }

    public Quarto criar(QuartoDTO dto) {
        Residencia residencia = buscarResidencia(dto.getResidenciaId());
        Quarto quarto = factory.criar(dto, residencia);
        return repository.save(quarto);
    }

    public Quarto atualizar(Long id, QuartoDTO dto) {
        Quarto quarto = buscarPorId(id);
        String tipo = factory.normalizarTipo(dto.getTipo());
        if (!quarto.getTipo().equals(tipo)) {
            throw new AlteracaoTipoQuartoNaoPermitidaException();
        }

        Residencia residencia = buscarResidencia(dto.getResidenciaId());
        factory.atualizar(quarto, dto, residencia);
        return repository.save(quarto);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }

    private Residencia buscarResidencia(Long residenciaId) {
        return residenciaRepository.findById(residenciaId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Residência não encontrada: " + residenciaId
            ));
    }
}
