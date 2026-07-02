package com.hospedagem.factory;

import com.hospedagem.dto.QuartoDTO;
import com.hospedagem.exception.AdicionalCamaInvalidoException;
import com.hospedagem.exception.ListaCamasObrigatoriaException;
import com.hospedagem.exception.NumeroCamasInvalidoException;
import com.hospedagem.exception.QuantidadeAmbientesInvalidaException;
import com.hospedagem.exception.TipoCamaObrigatorioException;
import com.hospedagem.exception.TipoQuartoInvalidoException;
import com.hospedagem.exception.ValorBaseInvalidoException;
import com.hospedagem.model.Quarto;
import com.hospedagem.model.QuartoDuplo;
import com.hospedagem.model.QuartoFamilia;
import com.hospedagem.model.QuartoIndividual;
import com.hospedagem.model.Residencia;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class QuartoFactory {

    public Quarto criar(QuartoDTO dto, Residencia residencia) {
        String tipo = normalizarTipo(dto.getTipo());
        Quarto quarto = switch (tipo) {
            case "INDIVIDUAL" -> new QuartoIndividual();
            case "DUPLO" -> new QuartoDuplo();
            case "FAMILIA" -> new QuartoFamilia();
            default -> throw TipoQuartoInvalidoException.valorInvalido();
        };
        preencher(quarto, dto, residencia);
        return quarto;
    }

    public void atualizar(Quarto quarto, QuartoDTO dto, Residencia residencia) {
        preencher(quarto, dto, residencia);
    }

    public String normalizarTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw TipoQuartoInvalidoException.naoInformado();
        }

        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.equals("INDIVIDUAL")
            && !normalizado.equals("DUPLO")
            && !normalizado.equals("FAMILIA")) {
            throw TipoQuartoInvalidoException.valorInvalido();
        }
        return normalizado;
    }

    private void preencher(Quarto quarto, QuartoDTO dto, Residencia residencia) {
        preencherCamposComuns(quarto, dto, residencia);

        switch (quarto.getTipo()) {
            case "INDIVIDUAL" -> preencherIndividual((QuartoIndividual) quarto, dto);
            case "DUPLO" -> preencherDuplo((QuartoDuplo) quarto, dto);
            case "FAMILIA" -> preencherFamilia((QuartoFamilia) quarto, dto);
            default -> throw TipoQuartoInvalidoException.valorInvalido();
        }
    }

    private void preencherCamposComuns(Quarto quarto, QuartoDTO dto, Residencia residencia) {
        if (dto.getValorBase() == null || dto.getValorBase() < 0) {
            throw new ValorBaseInvalidoException();
        }
        quarto.setValorBase(dto.getValorBase());
        quarto.setPossuiAR(dto.isPossuiAR());
        quarto.setPossuiHidro(dto.isPossuiHidro());
        residencia.adicionarQuarto(quarto);
    }

    private void preencherIndividual(QuartoIndividual quarto, QuartoDTO dto) {
        if (dto.getNumeroDeCamas() == null || dto.getNumeroDeCamas() < 1) {
            throw new NumeroCamasInvalidoException();
        }
        if (dto.getAdicionalPorCama() != null && dto.getAdicionalPorCama() < 0) {
            throw new AdicionalCamaInvalidoException();
        }
        quarto.setNumeroDeCamas(dto.getNumeroDeCamas());
        quarto.setAdicionalPorCama(dto.getAdicionalPorCama() == null ? 30.0 : dto.getAdicionalPorCama());
    }

    private void preencherDuplo(QuartoDuplo quarto, QuartoDTO dto) {
        if (dto.getTipoCama() == null) {
            throw new TipoCamaObrigatorioException();
        }
        quarto.setTipoCama(dto.getTipoCama());
        quarto.setPossuiBerco(Boolean.TRUE.equals(dto.getPossuiBerco()));
    }

    private void preencherFamilia(QuartoFamilia quarto, QuartoDTO dto) {
        if (dto.getListaDeCamas() == null || dto.getListaDeCamas().isEmpty()) {
            throw new ListaCamasObrigatoriaException();
        }
        if (dto.getQuantidadeDeAmbientes() == null || dto.getQuantidadeDeAmbientes() < 1) {
            throw new QuantidadeAmbientesInvalidaException();
        }
        quarto.setListaDeCamas(dto.getListaDeCamas());
        quarto.setQuantidadeDeAmbientes(dto.getQuantidadeDeAmbientes());
    }
}
