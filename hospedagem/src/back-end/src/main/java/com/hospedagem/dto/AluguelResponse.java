package com.hospedagem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospedagem.model.Aluguel;
import com.hospedagem.model.StatusAluguel;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AluguelResponse {

    private final Long id;
    private final ClienteResponse cliente;
    private final QuartoResponse quarto;
    private final LocalDateTime dataEntrada;
    private final LocalDateTime dataSaida;
    private final int numeroDeDiarias;
    private final int numeroDeHospedes;
    private final boolean solicitouBerco;
    private final Double valorTotal;
    private final StatusAluguel status;
    private final PagamentoResponse pagamento;

    public AluguelResponse(Aluguel aluguel) {
        this.id = aluguel.getId();
        this.cliente = aluguel.getCliente() == null ? null : new ClienteResponse(aluguel.getCliente());
        this.quarto = aluguel.getQuarto() == null ? null : QuartoResponse.semResidencia(aluguel.getQuarto());
        this.dataEntrada = aluguel.getDataEntrada();
        this.dataSaida = aluguel.getDataSaida();
        this.numeroDeDiarias = aluguel.getNumeroDeDiarias();
        this.numeroDeHospedes = aluguel.getNumeroDeHospedes();
        this.solicitouBerco = aluguel.isSolicitouBerco();
        this.valorTotal = aluguel.getValorTotal();
        this.status = aluguel.getStatus();
        this.pagamento = aluguel.getPagamento() == null
            ? null
            : new PagamentoResponse(aluguel.getPagamento());
    }

    public Long getId() { return id; }
    public ClienteResponse getCliente() { return cliente; }
    public QuartoResponse getQuarto() { return quarto; }
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public LocalDateTime getDataSaida() { return dataSaida; }
    public int getNumeroDeDiarias() { return numeroDeDiarias; }
    public int getNumeroDeHospedes() { return numeroDeHospedes; }
    public boolean isSolicitouBerco() { return solicitouBerco; }
    public Double getValorTotal() { return valorTotal; }
    public StatusAluguel getStatus() { return status; }
    public PagamentoResponse getPagamento() { return pagamento; }
}
