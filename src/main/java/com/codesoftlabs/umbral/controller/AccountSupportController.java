package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.BankDto;
import com.codesoftlabs.umbral.dto.CountryDto;
import com.codesoftlabs.umbral.dto.TimezoneDto;
import com.codesoftlabs.umbral.service.AccountSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts/support")
@Tag(name = "accounts-support", description = "Lookups for account creation: timezones, countries, and banks")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AccountSupportController {

    private final AccountSupportService accountSupportService;

    @GetMapping("/timezones")
    @Operation(summary = "Get all supported timezones", description = "Retrieves a list of all supported timezones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved timezones", content = @Content(schema = @Schema(implementation = TimezoneDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TimezoneDto>> getTimezones() {
        return ResponseEntity.ok(accountSupportService.getTimezones());
    }

    @GetMapping("/countries")
    @Operation(summary = "Get all supported countries", description = "Retrieves a list of all supported countries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved countries", content = @Content(schema = @Schema(implementation = CountryDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CountryDto>> getCountries() {
        return ResponseEntity.ok(accountSupportService.getCountries());
    }

    @GetMapping("/banks")
    @Operation(summary = "Get all supported banks", description = "Retrieves a list of all supported banks, optionally filtered by country")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved banks", content = @Content(schema = @Schema(implementation = BankDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BankDto>> getBanks() {
        return ResponseEntity.ok(accountSupportService.getBanks());
    }
}
