package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.hospedagem.model.TipoCama;
import com.hospedagem.model.TipoCamaFamilia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuartoDTO {

    @NotBlank
    private String tipo; // INDIVIDUAL, DUPLO ou FAMILIA

    @NotNull
    private Double valorBase;

    @JsonAlias("possuiAr")
    private boolean possuiAR;

    private boolean possuiHidro;

    @NotNull
    private Long residenciaId;

    // quarto individual
    @JsonAlias("numeroCamas")
    @Min(1)
    private Integer numeroDeCamas;

    private Double adicionalPorCama;

    // quarto duplo
    private TipoCama tipoCama;

    @JsonAlias("possuiBerco")
    private Boolean possuiBerco;

    // quarto familia
    private List<TipoCamaFamilia> listaDeCamas;

    @JsonAlias("quantidadeAmbientes")
    private Integer quantidadeDeAmbientes;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAR() { return possuiAR; }
    public void setPossuiAR(boolean possuiAR) { this.possuiAR = possuiAR; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Long getResidenciaId() { return residenciaId; }
    public void setResidenciaId(Long residenciaId) { this.residenciaId = residenciaId; }

    public Integer getNumeroDeCamas() { return numeroDeCamas; }
    public void setNumeroDeCamas(Integer numeroDeCamas) { this.numeroDeCamas = numeroDeCamas; }

    public Double getAdicionalPorCama() { return adicionalPorCama; }
    public void setAdicionalPorCama(Double adicionalPorCama) { this.adicionalPorCama = adicionalPorCama; }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public Boolean getPossuiBerco() { return possuiBerco; }
    public void setPossuiBerco(Boolean possuiBerco) { this.possuiBerco = possuiBerco; }

    public List<TipoCamaFamilia> getListaDeCamas() { return listaDeCamas; }
    public void setListaDeCamas(List<TipoCamaFamilia> listaDeCamas) { this.listaDeCamas = listaDeCamas; }

    public Integer getQuantidadeDeAmbientes() { return quantidadeDeAmbientes; }
    public void setQuantidadeDeAmbientes(Integer quantidadeDeAmbientes) { this.quantidadeDeAmbientes = quantidadeDeAmbientes; }
}
