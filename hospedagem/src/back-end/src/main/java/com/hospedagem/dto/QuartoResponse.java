package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.TipoCama;
import com.hospedagem.model.TipoCamaFamilia;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuartoResponse {

    private final Long id;
    private final Double valorBase;
    private final boolean possuiAR;
    private final boolean possuiHidro;
    private final String tipo;
    private final ResidenciaResponse residencia;
    private final Integer numeroDeCamas;
    private final Double adicionalPorCama;
    private final TipoCama tipoCama;
    private final Boolean possuiBerco;
    private final List<TipoCamaFamilia> listaDeCamas;
    private final Integer quantidadeDeAmbientes;
    private final Integer capacidadeMaxima;

    private QuartoResponse(Quarto quarto, boolean incluirResidencia) {
        this.id = quarto.getId();
        this.valorBase = quarto.getValorBase();
        this.possuiAR = quarto.isPossuiAR();
        this.possuiHidro = quarto.isPossuiHidro();
        this.tipo = quarto.getTipo();
        this.residencia = incluirResidencia && quarto.getResidencia() != null
            ? ResidenciaResponse.resumo(quarto.getResidencia())
            : null;

        this.numeroDeCamas = quarto.getNumeroDeCamas();
        this.adicionalPorCama = quarto.getAdicionalPorCama();
        this.tipoCama = quarto.getTipoCama();
        this.possuiBerco = quarto.getPossuiBerco();
        this.listaDeCamas = quarto.getListaDeCamas();
        this.quantidadeDeAmbientes = quarto.getQuantidadeDeAmbientes();
        this.capacidadeMaxima = quarto.getCapacidadeMaxima();
    }

    public static QuartoResponse completo(Quarto quarto) {
        return new QuartoResponse(quarto, true);
    }

    public static QuartoResponse semResidencia(Quarto quarto) {
        return new QuartoResponse(quarto, false);
    }

    public Long getId() { return id; }
    public Double getValorBase() { return valorBase; }
    public boolean isPossuiAR() { return possuiAR; }
    public boolean isPossuiHidro() { return possuiHidro; }
    public String getTipo() { return tipo; }
    public ResidenciaResponse getResidencia() { return residencia; }
    public Integer getNumeroDeCamas() { return numeroDeCamas; }
    public Double getAdicionalPorCama() { return adicionalPorCama; }
    public TipoCama getTipoCama() { return tipoCama; }
    public Boolean getPossuiBerco() { return possuiBerco; }
    public List<TipoCamaFamilia> getListaDeCamas() { return listaDeCamas; }
    public Integer getQuantidadeDeAmbientes() { return quantidadeDeAmbientes; }
    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
}
