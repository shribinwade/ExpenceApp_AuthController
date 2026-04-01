package org.example.Exception.handler;

import org.example.Exception.custom.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidation(ValidationException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("errors", ex.getErrors());

        return ResponseEntity.badRequest().body(response);
    }

}
