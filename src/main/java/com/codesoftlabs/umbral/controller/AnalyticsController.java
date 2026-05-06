package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "analytics", description = "Financial analysis and reporting")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/financial-health")
    @Operation(summary = "Get financial health summary", description = "Provides a high-level summary of the user's financial status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getFinancialHealth(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(analyticsService.getFinancialHealth(userId));
    }

    @GetMapping("/net-worth-trend")
    @Operation(summary = "Get net worth trend data", description = "Returns historical data for net worth visualization")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trend data retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> getNetWorthTrend(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(analyticsService.getNetWorthTrend(userId));
    }
}

