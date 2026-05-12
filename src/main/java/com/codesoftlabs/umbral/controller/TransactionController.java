package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.common.enums.TransactionType;
import com.codesoftlabs.umbral.dto.CreateTransactionDto;
import com.codesoftlabs.umbral.dto.PaginatedResponseDto;
import com.codesoftlabs.umbral.dto.UpdateTransactionDto;
import com.codesoftlabs.umbral.dto.TransactionDto;
import com.codesoftlabs.umbral.service.TransactionService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "transactions", description = "Manage financial transactions, receipts, and transaction analytics")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction with optional receipt attachment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created successfully", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> create(@RequestAttribute("userId") UUID userId,
                              @Valid @ModelAttribute CreateTransactionDto createTransactionDto,
                              @RequestPart(value = "receipt", required = false) MultipartFile file) throws ParseException {
        return ResponseEntity.ok(transactionService.create(userId, createTransactionDto, file));
    }

    @GetMapping
    @Operation(summary = "Get all transactions with filters", description = "Retrieves paginated transactions with optional filtering by type, category, account, date range, and search query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions", content = @Content(schema = @Schema(implementation = PaginatedResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaginatedResponseDto<TransactionDto>> findAll(@RequestAttribute("userId") UUID userId,
                                                     @RequestParam(required = false) @Parameter(description = "Filter by transaction type (EXPENSE or INCOME)") String type,
                                                     @RequestParam(required = false) @Parameter(description = "Filter by category ID") UUID categoryId,
                                                     @RequestParam(required = false) @Parameter(description = "Filter by account ID") UUID accountId,
                                                     @RequestParam(required = false) @Parameter(description = "Start date (ISO 8601 format)") String startDate,
                                                     @RequestParam(required = false) @Parameter(description = "End date (ISO 8601 format)") String endDate,
                                                     @RequestParam(required = false) @Parameter(description = "Search query for description") String search,
                                                     @RequestParam(defaultValue = "1") @Parameter(description = "Page number (1-indexed)") int page,
                                                     @RequestParam(defaultValue = "10") @Parameter(description = "Number of results per page") int limit) {
        if (type != null) {
            try {
                TransactionType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid transaction type: " + type);
            }
        }
        return ResponseEntity.ok(transactionService.findAll(userId, type, categoryId, accountId, startDate, endDate, search, page, limit));
    }

    @GetMapping("/summary/balance")
    @Operation(summary = "Get balance summary", description = "Retrieves overall balance summary across all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved balance summary", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getBalanceSummary(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(transactionService.getBalanceSummary(userId));
    }

    @GetMapping("/summary/categories")
    @Operation(summary = "Get category spending summary", description = "Retrieves spending breakdown by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved category summary", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> getCategorySummary(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(transactionService.getCategorySummary(userId));
    }

    @GetMapping("/summary/trend")
    @Operation(summary = "Get monthly cash flow trend", description = "Retrieves monthly trend analysis of income and expenses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trend data", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid range parameter"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> getMonthlyTrend(@RequestAttribute("userId") UUID userId,
                                                     @RequestParam(required = false) @Parameter(description = "Time range for trend analysis (e.g., 6m, 1y)") String range) {
        return ResponseEntity.ok(transactionService.getMonthlyTrend(userId, range));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific transaction", description = "Retrieves detailed information about a specific transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Transaction ID") UUID id) {
        return ResponseEntity.ok(transactionService.findOne(userId, id));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a transaction", description = "Updates an existing transaction and optionally updates the receipt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> update(@RequestAttribute("userId") UUID userId,
                              @PathVariable @Parameter(description = "Transaction ID") UUID id,
                              @Valid @ModelAttribute UpdateTransactionDto updateTransactionDto,
                              @RequestPart(value = "receipt", required = false) MultipartFile file) throws ParseException {
        return ResponseEntity.ok(transactionService.update(userId, id, updateTransactionDto, file));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction", description = "Deletes an existing transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Transaction ID") UUID id) {
        transactionService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a receipt for an existing transaction", description = "Attaches or updates a receipt image for a transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt uploaded successfully", content = @Content(schema = @Schema(implementation = TransactionDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> uploadReceipt(@RequestAttribute("userId") UUID userId,
                                     @PathVariable @Parameter(description = "Transaction ID") UUID id,
                                     @RequestPart("receipt") MultipartFile file) throws ParseException {
        return ResponseEntity.ok(transactionService.update(userId, id, new UpdateTransactionDto(), file));
    }
}
