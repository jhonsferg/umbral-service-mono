package com.codesoftlabs.umbral.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankSummaryDto {
    private UUID id;
    private String name;
    private String shortcut;
    private String logoUrl;
}
