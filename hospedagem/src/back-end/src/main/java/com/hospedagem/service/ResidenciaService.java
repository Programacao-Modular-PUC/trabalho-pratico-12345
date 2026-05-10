package com.hospedagem.service;

import com.hospedagem.dto.ResidenciaDTO;
import com.hospedagem.model.Residencia;
import com.hospedagem.repository.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResidenciaService {

    private final ResidenciaRepository repository;

    public ResidenciaService(ResidenciaRepository repository) {
        this.repository = repository;
    }

    public List<Residencia> listar() {
        return repository.findAll();
    }

    public Residencia buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Residência não encontrada: " + id));
    }

    public Residencia criar(ResidenciaDTO dto) {
        Residencia residencia = new Residencia();
        residencia.setNome(dto.getNome());
        residencia.setEndereco(dto.getEndereco());
        residencia.setDescricao(dto.getDescricao());
        return repository.save(residencia);
    }

    public Residencia atualizar(Long id, ResidenciaDTO dto) {
        Residencia residencia = buscarPorId(id);
        residencia.setNome(dto.getNome());
        residencia.setEndereco(dto.getEndereco());
        residencia.setDescricao(dto.getDescricao());
        return repository.save(residencia);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
}
