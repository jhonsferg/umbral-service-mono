package com.codesoftlabs.umbral.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumsDto {
    private Map<String, Object> transactionTypes;
    private Map<String, Object> categoryTypes;
    private Map<String, Object> accountTypes;
    private Map<String, Object> currencies;
    private Map<String, Object> goalTypes;
    private Map<String, Object> debtTypes;
    private Map<String, Object> investmentTypes;
    private Map<String, Object> frequencies;
}
