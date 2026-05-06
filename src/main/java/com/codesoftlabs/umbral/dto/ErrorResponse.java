package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private String errorCode;
    private int statusCode;
    private String path;
    private long timestamp;
    private Map<String, String> validationErrors;

    public ErrorResponse(String message, String errorCode, int statusCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(String message, String errorCode, int statusCode, String path) {
        this.message = message;
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }
}
