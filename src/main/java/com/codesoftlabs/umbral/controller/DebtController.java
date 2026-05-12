package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CreateDebtDto;
import com.codesoftlabs.umbral.dto.DebtPaymentRequestDto;
import com.codesoftlabs.umbral.dto.TransactionDto;
import com.codesoftlabs.umbral.dto.response.DebtResponseDto;
import com.codesoftlabs.umbral.service.DebtsService;
import com.codesoftlabs.umbral.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/debts")
@Tag(name = "debts", description = "Manage personal debts and track debt payments")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class DebtController {
    private final DebtsService debtsService;

    @PostMapping
    @Operation(summary = "Create a new debt", description = "Creates a new debt record for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Debt created successfully", content = @Content(schema = @Schema(implementation = DebtResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DebtResponseDto> create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateDebtDto dto) {
        return ResponseEntity.ok(debtsService.create(userId, dto));
    }

    @GetMapping
    @Operation(summary = "Get all debts for the current user", description = "Retrieves all debts with detailed information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all debts", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> findAll(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(debtsService.findAll(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific debt", description = "Retrieves details of a specific debt by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Debt retrieved successfully", content = @Content(schema = @Schema(implementation = DebtResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Debt not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DebtResponseDto> findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Debt ID") UUID id) {
        return ResponseEntity.ok(debtsService.findOneDto(userId, id));
    }

    @PostMapping("/{id}/payments")
    @Operation(summary = "Record a debt payment", description = "Records a payment transaction for an existing debt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment recorded successfully", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payment amount or account"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Debt or account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> recordPayment(
            @RequestAttribute("userId") UUID userId,
            @PathVariable @Parameter(description = "Debt ID") UUID id,
            @Valid @RequestBody DebtPaymentRequestDto dto) {

        return ResponseEntity.ok(debtsService.recordPayment(userId, id, BigDecimal.valueOf(dto.getAmount()),
                UUID.fromString(dto.getAccountId()), dto.getNotes()));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a debt", description = "Updates an existing debt record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Debt updated successfully", content = @Content(schema = @Schema(implementation = DebtResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Debt not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DebtResponseDto> update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Debt ID") UUID id, @Valid @RequestBody CreateDebtDto dto) {
        return ResponseEntity.ok(debtsService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a debt", description = "Deletes an existing debt record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Debt deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Debt not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Debt ID") UUID id) {
        debtsService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }
}
