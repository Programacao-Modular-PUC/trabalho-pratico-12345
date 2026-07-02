package com.hospedagem.controller;

import com.hospedagem.dto.PagamentoResponse;
import com.hospedagem.service.PagamentoService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/confirmar")
    public PagamentoResponse confirmar(@PathVariable Long id) {
        return new PagamentoResponse(service.confirmar(id));
    }
}
