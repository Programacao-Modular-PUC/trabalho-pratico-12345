package com.hospedagem.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<Map<String, String>> handleNegocio(NegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflito(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("erro", "Dado duplicado ou inválido. Verifique se os campos únicos (como CPF) já existem no sistema."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, Object> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
            erros.put(fe.getField(), fe.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(erros);
    }
}

