package com.hospedagem.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residencias")
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @OneToMany
    @JoinColumn(name = "residencia_id")
    private List<Quarto> quartos = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Quarto> getQuartos() { return quartos; }
    public void setQuartos(List<Quarto> quartos) { this.quartos = quartos; }
}
