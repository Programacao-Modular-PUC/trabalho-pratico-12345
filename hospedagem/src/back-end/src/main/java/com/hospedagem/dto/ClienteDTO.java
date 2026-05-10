package com.hospedagem.dto;

import jakarta.validation.constraints.NotBlank;

public class ClienteDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String cpf;

    private String email;
    private String telefone;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
