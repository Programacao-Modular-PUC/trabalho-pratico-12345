package com.hospedagem.dto;

import com.hospedagem.model.Cliente;

public class ClienteResponse {

    private final Long id;
    private final String nome;
    private final String cpf;
    private final String email;
    private final String telefone;

    public ClienteResponse(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.cpf = cliente.getCpf();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
}
