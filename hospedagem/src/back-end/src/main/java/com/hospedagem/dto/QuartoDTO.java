package com.hospedagem.dto;

import com.hospedagem.model.TipoCama;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuartoDTO {

    @NotBlank
    private String tipo; // "individual" | "duplo" | "familia"

    @NotNull
    private Double valorBase;

    private boolean possuiAr;
    private boolean possuiHidro;

    // individual
    @Min(1)
    private Integer numeroCamas;

    // duplo
    private TipoCama tipoCama;
    private Boolean possuiBerco;

    // familia
    private Integer camasSolteiro;
    private Integer camasCasal;
    private Integer camasQueenKing;
    private Integer quantidadeAmbientes;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAr() { return possuiAr; }
    public void setPossuiAr(boolean possuiAr) { this.possuiAr = possuiAr; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Integer getNumeroCamas() { return numeroCamas; }
    public void setNumeroCamas(Integer numeroCamas) { this.numeroCamas = numeroCamas; }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public Boolean getPossuiBerco() { return possuiBerco; }
    public void setPossuiBerco(Boolean possuiBerco) { this.possuiBerco = possuiBerco; }

    public Integer getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public Integer getCamasCasal() { return camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(Integer quantidadeAmbientes) { this.quantidadeAmbientes = quantidadeAmbientes; }
}
