package com.codesoftlabs.umbral.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {
    private UUID id;
    private String code;
    private String symbol;
    private String name;
    private String namePlural;
    private Integer decimalPlaces;
}
