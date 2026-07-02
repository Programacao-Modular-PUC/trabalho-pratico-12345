package com.hospedagem.config;

import java.time.LocalTime;
import java.time.ZoneId;

public final class ConfiguracaoGlobalHospedagem {

    private final LocalTime horarioBaseDiaria;
    private final ZoneId timezone;
    private final double adicionalArCondicionado;
    private final double adicionalHidromassagem;
    private final double taxaBerco;
    private final double percentualAltaTemporada;
    private final double percentualBaixaTemporada;
    private final double percentualFeriado;
    private final double percentualClienteFrequente;

    private ConfiguracaoGlobalHospedagem() {
        this.horarioBaseDiaria = LocalTime.NOON;
        this.timezone = ZoneId.of("America/Sao_Paulo");
        this.adicionalArCondicionado = 40.0;
        this.adicionalHidromassagem = 80.0;
        this.taxaBerco = 25.0;
        this.percentualAltaTemporada = 0.20;
        this.percentualBaixaTemporada = 0.10;
        this.percentualFeriado = 0.15;
        this.percentualClienteFrequente = 0.10;
    }

    private static class Holder {
        private static final ConfiguracaoGlobalHospedagem INSTANCIA =
            new ConfiguracaoGlobalHospedagem();
    }

    public static ConfiguracaoGlobalHospedagem getInstance() {
        return Holder.INSTANCIA;
    }

    public LocalTime getHorarioBaseDiaria() { return horarioBaseDiaria; }
    public ZoneId getTimezone() { return timezone; }
    public double getAdicionalArCondicionado() { return adicionalArCondicionado; }
    public double getAdicionalHidromassagem() { return adicionalHidromassagem; }
    public double getTaxaBerco() { return taxaBerco; }
    public double getPercentualAltaTemporada() { return percentualAltaTemporada; }
    public double getPercentualBaixaTemporada() { return percentualBaixaTemporada; }
    public double getPercentualFeriado() { return percentualFeriado; }
    public double getPercentualClienteFrequente() { return percentualClienteFrequente; }
}
