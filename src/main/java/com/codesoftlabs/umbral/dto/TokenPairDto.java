package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenPairDto {
    private String accessToken;
    private String refreshToken;
    private UserDto user;
    private Boolean requireMfa;
    private String userId;
    private String message;
}
