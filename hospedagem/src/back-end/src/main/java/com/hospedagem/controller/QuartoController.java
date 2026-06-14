package com.hospedagem.controller;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.model.Quarto;
import com.hospedagem.service.QuartoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Quarto> listar(@RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            return service.listarPorTipo(tipo);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public Quarto buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Quarto> criar(@RequestBody @Valid QuartoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public Quarto atualizar(@PathVariable Long id, @RequestBody @Valid QuartoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
