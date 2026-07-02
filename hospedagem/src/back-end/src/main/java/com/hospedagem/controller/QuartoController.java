package com.hospedagem.controller;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.dto.QuartoResponse;
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
    public List<QuartoResponse> listar(@RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            return service.listarPorTipo(tipo).stream().map(QuartoResponse::completo).toList();
        }
        return service.listar().stream().map(QuartoResponse::completo).toList();
    }

    @GetMapping("/{id}")
    public QuartoResponse buscar(@PathVariable Long id) {
        return QuartoResponse.completo(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<QuartoResponse> criar(@RequestBody @Valid QuartoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(QuartoResponse.completo(service.criar(dto)));
    }

    @PutMapping("/{id}")
    public QuartoResponse atualizar(@PathVariable Long id, @RequestBody @Valid QuartoDTO dto) {
        return QuartoResponse.completo(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
