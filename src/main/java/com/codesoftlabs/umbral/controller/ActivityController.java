package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.entity.ActivityLog;
import com.codesoftlabs.umbral.service.ActivityService;
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
@RequestMapping("/api/v1/activity")
@Tag(name = "activity", description = "Track user activity and events")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Get user activity logs", description = "Retrieves a list of activity logs for the current user with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity logs retrieved successfully", content = @Content(schema = @Schema(implementation = ActivityLog.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ActivityLog>> findAll(
            @RequestAttribute("userId") UUID userId,
            @RequestParam(value = "limit", required = false, defaultValue = "20") @Parameter(description = "Maximum number of activity logs to retrieve (default: 20)") int limit) {
        return ResponseEntity.ok(activityService.findAll(userId, limit));
    }
}
