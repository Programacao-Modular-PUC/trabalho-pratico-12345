package com.hospedagem.service;

import com.hospedagem.exception.DataInvalidaException;
import com.hospedagem.config.ConfiguracaoGlobalHospedagem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Component;

@Component
public class CalculadoraDiarias {

    public int calcular(LocalDateTime dataEntrada, LocalDateTime dataSaida) {
        if (dataEntrada == null || dataSaida == null) {
            throw new DataInvalidaException("Datas de entrada e saída devem ser informadas.");
        }
        if (!dataSaida.isAfter(dataEntrada)) {
            throw new DataInvalidaException(
                "Data de saída deve ser posterior à data de entrada."
            );
        }

        LocalTime horarioBase = ConfiguracaoGlobalHospedagem.getInstance().getHorarioBaseDiaria();
        LocalDate inicioDaPrimeiraDiaria = dataEntrada.toLocalDate();
        if (dataEntrada.toLocalTime().isBefore(horarioBase)) {
            inicioDaPrimeiraDiaria = inicioDaPrimeiraDiaria.minusDays(1);
        }

        LocalDate fimDaUltimaDiaria = dataSaida.toLocalDate();
        if (dataSaida.toLocalTime().isAfter(horarioBase)) {
            fimDaUltimaDiaria = fimDaUltimaDiaria.plusDays(1);
        }

        long diarias = ChronoUnit.DAYS.between(inicioDaPrimeiraDiaria, fimDaUltimaDiaria);
        return Math.toIntExact(Math.max(1, diarias));
    }
}
