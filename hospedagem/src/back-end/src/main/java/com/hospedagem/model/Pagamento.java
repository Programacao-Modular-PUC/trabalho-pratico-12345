package com.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hospedagem.exception.PagamentoJaConfirmadoException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "aluguel_id", nullable = false, unique = true)
    @JsonIgnore
    private Aluguel aluguel;

    private Double valor;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    private LocalDateTime dataConfirmacao;

    public void confirmar(LocalDateTime dataConfirmacao) {
        if (status == StatusPagamento.CONFIRMADO) {
            throw new PagamentoJaConfirmadoException();
        }
        this.status = StatusPagamento.CONFIRMADO;
        this.dataConfirmacao = dataConfirmacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluguel getAluguel() { return aluguel; }
    public void setAluguel(Aluguel aluguel) { this.aluguel = aluguel; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }

    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDateTime dataConfirmacao) { this.dataConfirmacao = dataConfirmacao; }
}
