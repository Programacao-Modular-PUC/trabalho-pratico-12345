package com.hospedagem.controller;

import com.hospedagem.dto.ResidenciaDTO;
import com.hospedagem.model.Residencia;
import com.hospedagem.service.ResidenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/residencias")
public class ResidenciaController {

    private final ResidenciaService service;

    public ResidenciaController(ResidenciaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Residencia> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Residencia buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Residencia> criar(@RequestBody @Valid ResidenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public Residencia atualizar(@PathVariable Long id, @RequestBody @Valid ResidenciaDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
