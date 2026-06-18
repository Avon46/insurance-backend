package com.insurance.insurance_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(
                        ResourceNotFoundException ex) {
                ErrorResponse response = new ErrorResponse(
                                false,
                                ex.getMessage(),
                                ex.getErrorCode(),
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(response);
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(
                        BusinessException ex) {
                ErrorResponse response = new ErrorResponse(
                                false,
                                ex.getMessage(),
                                ex.getErrorCode(),
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex) {
                ErrorResponse response = new ErrorResponse(
                                false,
                                ex.getMessage(),
                                "INVALID_ARGUMENT",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(response);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception ex) {
                ex.printStackTrace();
                ErrorResponse response = new ErrorResponse(
                                false,
                                "系統發生錯誤，請稍後再試",
                                "INTERNAL_SERVER_ERROR",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(response);
        }
}