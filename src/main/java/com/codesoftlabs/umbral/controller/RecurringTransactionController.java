package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CreateRecurringTransactionDto;
import com.codesoftlabs.umbral.dto.UpdateRecurringTransactionDto;
import com.codesoftlabs.umbral.entity.RecurringTransaction;
import com.codesoftlabs.umbral.service.RecurringTransactionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recurring")
@Tag(name = "recurring", description = "Manage recurring transaction rules and automated transaction processing")
@SecurityRequirement(name = "bearerAuth")
public class RecurringTransactionController {
    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @PostMapping
    @Operation(summary = "Create a new recurring transaction rule", description = "Creates a new rule for automatically generating recurring transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction rule created successfully", content = @Content(schema = @Schema(implementation = RecurringTransaction.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public RecurringTransaction create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateRecurringTransactionDto dto) {
        return recurringTransactionService.create(userId, dto);
    }

    @GetMapping
    @Operation(summary = "List all recurring transaction rules", description = "Retrieves all recurring transaction rules for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved recurring transaction rules", content = @Content(schema = @Schema(implementation = RecurringTransaction.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<RecurringTransaction> findAll(@RequestAttribute("userId") UUID userId) {
        return recurringTransactionService.findAll(userId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recurring transaction rule", description = "Deletes an existing recurring transaction rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recurring transaction rule deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Recurring transaction rule not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Recurring transaction rule ID") UUID id) {
        recurringTransactionService.remove(userId, id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a recurring transaction rule", description = "Updates an existing recurring transaction rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction rule updated successfully", content = @Content(schema = @Schema(implementation = RecurringTransaction.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Recurring transaction rule not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public RecurringTransaction update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Recurring transaction rule ID") UUID id, @Valid @RequestBody UpdateRecurringTransactionDto dto) {
        return recurringTransactionService.update(userId, id, dto);
    }

    @PostMapping("/process")
    @Operation(summary = "Manually trigger the recurring transaction processor", description = "Manually processes all pending recurring transactions (development/testing endpoint)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurring transaction processor executed successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void process() {
        recurringTransactionService.processRecurringTransactions();
    }
}
