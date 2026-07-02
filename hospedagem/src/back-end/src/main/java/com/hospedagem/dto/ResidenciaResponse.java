package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospedagem.model.Residencia;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResidenciaResponse {

    private final Long id;
    private final String nome;
    private final String endereco;
    private final String descricao;
    private final List<QuartoResponse> quartos;

    public ResidenciaResponse(Residencia residencia) {
        this(residencia, true);
    }

    private ResidenciaResponse(Residencia residencia, boolean incluirQuartos) {
        this.id = residencia.getId();
        this.nome = residencia.getNome();
        this.endereco = residencia.getEndereco();
        this.descricao = residencia.getDescricao();
        this.quartos = incluirQuartos
            ? residencia.getQuartos().stream().map(QuartoResponse::semResidencia).toList()
            : null;
    }

    public static ResidenciaResponse resumo(Residencia residencia) {
        return new ResidenciaResponse(residencia, false);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getDescricao() { return descricao; }
    public List<QuartoResponse> getQuartos() { return quartos; }
}
