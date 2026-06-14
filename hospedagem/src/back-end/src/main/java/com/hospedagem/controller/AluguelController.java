package com.hospedagem.controller;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.model.Aluguel;
import com.hospedagem.service.AluguelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService service;

    public AluguelController(AluguelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Aluguel> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Aluguel buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Aluguel> criar(@RequestBody @Valid AluguelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public Aluguel atualizar(@PathVariable Long id, @RequestBody @Valid AluguelDTO dto) {
        return service.atualizar(id, dto);
    }

    // Cancelamento de aluguel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Histórico por cliente
    @GetMapping("/cliente/{clienteId}")
    public List<Aluguel> historicoPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }
}
