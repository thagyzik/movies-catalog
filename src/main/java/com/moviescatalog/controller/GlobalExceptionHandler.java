package com.moviescatalog.controller;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getReason());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Invalid request. Please check if the submitted data is correct.");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleEnumConversionError(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            error.put(ex.getName(), String.format(
                    "Invalid value '%s' for parameter '%s'. Allowed values are: %s",
                    ex.getValue(),
                    ex.getName(),
                    Arrays.toString(ex.getRequiredType().getEnumConstants())
            ));
        } else {
            error.put(ex.getName(), "Invalid parameter.");
        }

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                                                               errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

}
