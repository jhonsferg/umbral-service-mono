package com.codesoftlabs.umbral.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class UpdateAccountDto {
    private String name;
    private String type;
    private String currency;
    private Double balance;
    private String color;
    private String icon;
    private UUID bankId;
}
