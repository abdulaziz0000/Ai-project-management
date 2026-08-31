package com.project_management.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .badRequest()
                .body(
                        e.getClass().getSimpleName()
                                + " : "
                                + e.getMessage()
                );
    }

}
