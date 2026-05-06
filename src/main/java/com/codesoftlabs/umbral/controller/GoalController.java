package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CreateGoalDto;
import com.codesoftlabs.umbral.entity.Goal;
import com.codesoftlabs.umbral.service.GoalsService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@Tag(name = "goals", description = "Manage financial goals and track progress towards savings targets")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class GoalController {
    private final GoalsService goalsService;

    @PostMapping
    @Operation(summary = "Create a new financial goal", description = "Creates a new savings goal for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal created successfully", content = @Content(schema = @Schema(implementation = Goal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Goal> create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateGoalDto dto) {
        return ResponseEntity.ok(goalsService.create(userId, dto));
    }

    @GetMapping
    @Operation(summary = "Get all financial goals for the current user", description = "Retrieves all goals with progress tracking information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all goals", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Map<String, Object>>> findAll(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(goalsService.findAll(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific financial goal", description = "Retrieves details of a specific goal by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal retrieved successfully", content = @Content(schema = @Schema(implementation = Goal.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Goal> findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Goal ID") UUID id) {
        return ResponseEntity.ok(goalsService.findOne(userId, id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a financial goal", description = "Updates an existing goal record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Goal updated successfully", content = @Content(schema = @Schema(implementation = Goal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Goal> update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Goal ID") UUID id, @Valid @RequestBody CreateGoalDto dto) {
        return ResponseEntity.ok(goalsService.update(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a financial goal", description = "Deletes an existing goal record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Goal not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Goal ID") UUID id) {
        goalsService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }
}
