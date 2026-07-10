package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.service.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/activity/backup")
@Tag(name = "backup", description = "User data backup and export")
@SecurityRequirement(name = "bearerAuth")
public class BackupController {
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping
    @Operation(summary = "Export user data", description = "Exports all user data for backup or migration purposes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data exported successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> exportData(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(backupService.getUserData(userId));
    }
}
