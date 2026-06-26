package br.com.ecommerce.infrastructure.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Tratamento para violações de regra de negócio (ex: SKU inativo)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Regra de Negócio Violada", ex.getMessage());
    }

    // Tratamento para argumentos inválidos (ex: quantidade negativa)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponseEntity(HttpStatus.BAD_REQUEST, "Erro de Validação de Dados", ex.getMessage());
    }

    // Tratamento genérico para erros de sistema (ex: SKU não encontrado)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        // Se a mensagem contiver "não encontrado", retorna 404, caso contrário 500
        HttpStatus status = ex.getMessage().toLowerCase().contains("não encontrado") 
                            ? HttpStatus.NOT_FOUND 
                            : HttpStatus.INTERNAL_SERVER_ERROR;
        
        String errorType = (status == HttpStatus.NOT_FOUND) ? "Recurso não encontrado" : "Erro Interno no Servidor";
        return buildResponseEntity(status, errorType, ex.getMessage());
    }

    // Método auxiliar centralizado
    private ResponseEntity<Map<String, Object>> buildResponseEntity(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}