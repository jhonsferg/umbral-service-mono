package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CurrencyDto;
import com.codesoftlabs.umbral.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metadata")
@Tag(name = "metadata", description = "Retrieve application metadata including currencies and enums")
public class MetadataController {
    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get all supported currencies", description = "Retrieves a list of all supported currencies for account and transaction operations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies", content = @Content(schema = @Schema(implementation = CurrencyDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CurrencyDto>> getCurrencies() {
        return ResponseEntity.ok(metadataService.getCurrencies());
    }

    @GetMapping("/enums")
    @Operation(summary = "Get all common enums for forms", description = "Retrieves enumeration values used in forms such as transaction types, frequencies, and statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved enums", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getEnums() {
        return ResponseEntity.ok(metadataService.getEnums());
    }
}
