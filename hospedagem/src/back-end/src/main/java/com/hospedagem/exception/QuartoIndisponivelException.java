package com.hospedagem.exception;

public class QuartoIndisponivelException extends RuntimeException {

    private final Long idQuarto;

    public QuartoIndisponivelException(Long idQuarto) {
        super("Quarto de id " + idQuarto + " não está disponível para o período solicitado.");
        this.idQuarto = idQuarto;
    }

    public QuartoIndisponivelException(Long idQuarto, String motivo) {
        super("Quarto de id " + idQuarto + " não está disponível: " + motivo);
        this.idQuarto = idQuarto;
    }

    public Long getIdQuarto() {
        return idQuarto;
    }
}
