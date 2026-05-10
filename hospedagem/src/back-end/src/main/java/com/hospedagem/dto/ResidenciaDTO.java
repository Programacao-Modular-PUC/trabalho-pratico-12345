package com.hospedagem.dto;

import jakarta.validation.constraints.NotBlank;

public class ResidenciaDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String endereco;

    private String descricao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
