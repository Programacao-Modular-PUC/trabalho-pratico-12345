package com.hospedagem.exception;

public class RecursoNaoPermitidoException extends RuntimeException {

    private final String recurso;
    private final String tipoQuarto;

    public RecursoNaoPermitidoException(String recurso, String tipoQuarto) {
        super("Recurso '" + recurso + "' não é permitido em " + tipoQuarto + ".");
        this.recurso = recurso;
        this.tipoQuarto = tipoQuarto;
    }

    public RecursoNaoPermitidoException(String recurso, String tipoQuarto, String motivo) {
        super("Recurso '" + recurso + "' não é permitido em " + tipoQuarto + ": " + motivo + ".");
        this.recurso = recurso;
        this.tipoQuarto = tipoQuarto;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getTipoQuarto() {
        return tipoQuarto;
    }
}
