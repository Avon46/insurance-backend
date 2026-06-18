package com.insurance.insurance_backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private Boolean success;

    private String message;

    private String errorCode;

    private String timestamp;
}