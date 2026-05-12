package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.AccountDto;
import com.codesoftlabs.umbral.dto.CreateAccountDto;
import com.codesoftlabs.umbral.dto.UpdateAccountDto;
import com.codesoftlabs.umbral.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "accounts", description = "Manage user financial accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @Operation(summary = "Create a new financial account", description = "Creates a new account for the current user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully", content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDto> create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateAccountDto dto) {
        return ResponseEntity.ok(accountService.create(userId, dto));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for the current user", description = "Retrieves all financial accounts associated with the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all accounts", content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AccountDto>> findAll(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(accountService.findAll(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single account by ID", description = "Retrieves a specific account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully", content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDto> findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Account ID") UUID id) {
        return ResponseEntity.ok(accountService.findOne(userId, id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an account", description = "Updates an existing account with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content(schema = @Schema(implementation = AccountDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AccountDto> update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Account ID") UUID id, @Valid @RequestBody UpdateAccountDto dto) {
        return ResponseEntity.ok(accountService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an account", description = "Deletes an existing account and all its associated data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Account ID") UUID id) {
        accountService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }
}
