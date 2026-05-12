package com.codesoftlabs.umbral.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecurringTransactionDto {
    private UUID id;
    private String name;
    private String description;
    private String type;
    private String frequency;
    private LocalDateTime nextOccurrenceDate;
    private LocalDateTime endDate;
    private String status;
    private Boolean autoGenerate;
    private UUID categoryId;
    private CategoryDto category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
