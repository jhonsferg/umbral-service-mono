package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CreateAssetDto;
import com.codesoftlabs.umbral.dto.response.AssetResponseDto;
import com.codesoftlabs.umbral.service.AssetsService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assets")
@Tag(name = "assets", description = "Manage user assets and investment portfolio")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AssetsController {
    private final AssetsService assetsService;

    @PostMapping
    @Operation(summary = "Create a new asset", description = "Creates a new asset entry in the user's portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset created successfully", content = @Content(schema = @Schema(implementation = AssetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssetResponseDto> create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateAssetDto dto) {
        return ResponseEntity.ok(assetsService.create(userId, dto));
    }

    @GetMapping
    @Operation(summary = "Get all assets for the current user", description = "Retrieves a list of all assets in the user's portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assets retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> findAll(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(assetsService.findAll(userId));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get portfolio summary", description = "Retrieves a summary of the portfolio including total value and allocations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getSummary(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(assetsService.getPortfolioSummary(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific asset by ID", description = "Retrieves details of a specific asset in the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset retrieved successfully", content = @Content(schema = @Schema(implementation = AssetResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssetResponseDto> findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Asset ID") UUID id) {
        return ResponseEntity.ok(assetsService.findOneDto(userId, id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an asset", description = "Updates details of an existing asset in the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset updated successfully", content = @Content(schema = @Schema(implementation = AssetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<AssetResponseDto> update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Asset ID") UUID id, @Valid @RequestBody CreateAssetDto dto) {
        return ResponseEntity.ok(assetsService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an asset", description = "Removes an asset from the user's portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asset deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Asset ID") UUID id) {
        assetsService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }
}
