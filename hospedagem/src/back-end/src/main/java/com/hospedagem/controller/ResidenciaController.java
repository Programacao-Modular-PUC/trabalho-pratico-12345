package com.hospedagem.controller;

import com.hospedagem.dto.ResidenciaDTO;
import com.hospedagem.dto.ResidenciaResponse;
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
    public List<ResidenciaResponse> listar() {
        return service.listar().stream().map(ResidenciaResponse::new).toList();
    }

    @GetMapping("/{id}")
    public ResidenciaResponse buscar(@PathVariable Long id) {
        return new ResidenciaResponse(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResidenciaResponse> criar(@RequestBody @Valid ResidenciaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ResidenciaResponse(service.criar(dto)));
    }

    @PutMapping("/{id}")
    public ResidenciaResponse atualizar(@PathVariable Long id, @RequestBody @Valid ResidenciaDTO dto) {
        return new ResidenciaResponse(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
