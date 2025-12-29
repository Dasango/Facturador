package com.uce.emprendimiento.backend.exception;

import com.uce.emprendimiento.backend.dto.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponse> handleException(Exception e) {
        return new ResponseEntity<>(new AuthResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AuthResponse> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(new AuthResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
    }
}
