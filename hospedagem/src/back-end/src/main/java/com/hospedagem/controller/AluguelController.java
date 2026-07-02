package com.hospedagem.controller;

import com.hospedagem.dto.AluguelDTO;
import com.hospedagem.dto.AluguelResponse;
import com.hospedagem.dto.ReciboDTO;
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
    public List<AluguelResponse> listar() {
        return service.listar().stream().map(AluguelResponse::new).toList();
    }

    @GetMapping("/{id}")
    public AluguelResponse buscar(@PathVariable Long id) {
        return new AluguelResponse(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AluguelResponse> criar(@RequestBody @Valid AluguelDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AluguelResponse(service.criar(dto)));
    }

    @PutMapping("/{id}")
    public AluguelResponse atualizar(@PathVariable Long id, @RequestBody @Valid AluguelDTO dto) {
        return new AluguelResponse(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public AluguelResponse cancelar(@PathVariable Long id) {
        return new AluguelResponse(service.cancelar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Histórico por cliente
    @GetMapping("/cliente/{clienteId}")
    public List<AluguelResponse> historicoPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId).stream().map(AluguelResponse::new).toList();
    }

    @GetMapping("/residencia/{residenciaId}")
    public List<AluguelResponse> historicoPorResidencia(@PathVariable Long residenciaId) {
        return service.listarPorResidencia(residenciaId).stream().map(AluguelResponse::new).toList();
    }

    @GetMapping("/{id}/recibo")
    public ReciboDTO gerarRecibo(@PathVariable Long id) {
        return service.gerarRecibo(id);
    }
}
