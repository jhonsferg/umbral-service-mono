package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.BudgetUpdateRequestDto;
import com.codesoftlabs.umbral.dto.CreateBudgetDto;
import com.codesoftlabs.umbral.entity.Budget;
import com.codesoftlabs.umbral.service.BudgetService;
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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@Tag(name = "budgets", description = "Manage user budgets and spending limits")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @Operation(summary = "Create a new budget", description = "Creates a new budget with spending limits for a specific category or time period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget created successfully", content = @Content(schema = @Schema(implementation = Budget.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Budget create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateBudgetDto createBudgetDto) {
        return budgetService.create(userId, createBudgetDto);
    }

    @GetMapping
    @Operation(summary = "Get all budgets for a specific month/year", description = "Retrieves all budgets for the specified month and year, or current month if not provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all budgets", content = @Content(schema = @Schema(implementation = Budget.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Budget> findAll(@RequestAttribute("userId") UUID userId,
                                @RequestParam(required = false) @Parameter(description = "Month (1-12)") Integer month,
                                @RequestParam(required = false) @Parameter(description = "Year") Integer year) {
        return budgetService.findAll(userId, month, year);
    }

    @GetMapping("/progress")
    @Operation(summary = "Get budget progress with spending data")
    public List<Map<String, Object>> getProgress(@RequestAttribute("userId") UUID userId,
                                                 @RequestParam(required = false) Integer month,
                                                 @RequestParam(required = false) Integer year) {
        return budgetService.getBudgetProgress(userId, month, year);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update budget amount", description = "Updates the spending limit amount for an existing budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget updated successfully", content = @Content(schema = @Schema(implementation = Budget.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Budget update(@RequestAttribute("userId") UUID userId,
                         @PathVariable @Parameter(description = "Budget ID") UUID id,
                         @Valid @RequestBody BudgetUpdateRequestDto dto) {
        return budgetService.update(userId, id, dto.getAmount());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget", description = "Deletes an existing budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Budget ID") UUID id) {
        budgetService.remove(userId, id);
    }
}
