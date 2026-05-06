package com.codesoftlabs.umbral.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int statusCode;
    private String errorCode;
    private String message;
    private List<ValidationError> errors;
    private LocalDateTime timestamp;
    private String path;

    public static ErrorResponse of(int statusCode, String errorCode, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse ofValidation(String message, List<ValidationError> errors) {
        return ErrorResponse.builder()
                .statusCode(400)
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
