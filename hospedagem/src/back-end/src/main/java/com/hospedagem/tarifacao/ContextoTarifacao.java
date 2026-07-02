package com.hospedagem.tarifacao;

import com.hospedagem.model.Cliente;
import java.time.LocalDateTime;

public final class ContextoTarifacao {

    private final LocalDateTime dataEntrada;
    private final LocalDateTime dataSaida;
    private final Cliente cliente;
    private final long quantidadeAlugueisAnteriores;

    public ContextoTarifacao(LocalDateTime dataEntrada,
                             LocalDateTime dataSaida,
                             Cliente cliente,
                             long quantidadeAlugueisAnteriores) {
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.cliente = cliente;
        this.quantidadeAlugueisAnteriores = quantidadeAlugueisAnteriores;
    }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public Cliente getCliente() { return cliente; }
    public long getQuantidadeAlugueisAnteriores() { return quantidadeAlugueisAnteriores; }
}
