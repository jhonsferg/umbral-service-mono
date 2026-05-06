package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.InvitationRequestDto;
import com.codesoftlabs.umbral.service.InvitationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
@Tag(name = "invitations", description = "Manage account invitations and shared access")
public class InvitationsController {
    private final InvitationsService invitationsService;

    public InvitationsController(InvitationsService invitationsService) {
        this.invitationsService = invitationsService;
    }

    @PostMapping
    @Operation(summary = "Create an invitation", description = "Creates a new invitation for sharing account access with another user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation created successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody InvitationRequestDto dto) {
        return ResponseEntity.ok(invitationsService.create(userId, dto.getAccountId(), dto.getEmail(), dto.getRole()));
    }

    @GetMapping("/sent")
    @Operation(summary = "Get sent invitations", description = "Retrieves all invitations sent by the current user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved sent invitations", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> findAllSent(@RequestAttribute("userId") UUID userId) {
        return ResponseEntity.ok(invitationsService.findAllSent(userId));
    }

    @GetMapping("/received")
    @Operation(summary = "Get received invitations", description = "Retrieves all invitations received by the current user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved received invitations", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> findAllReceived(@RequestAttribute("userId") UUID userId, @RequestAttribute("userEmail") String userEmail) {
        return ResponseEntity.ok(invitationsService.findAllReceived(userId, userEmail));
    }

    @GetMapping("/{token}")
    @Operation(summary = "Get invitation details by token", description = "Retrieves invitation details using an invitation token (public endpoint)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation details retrieved successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Invitation not found or token expired"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> findByToken(@PathVariable @Parameter(description = "Invitation token") String token) {
        return ResponseEntity.ok(invitationsService.findByToken(token));
    }

    @PostMapping("/{token}/accept")
    @Operation(summary = "Accept an invitation", description = "Accepts an invitation to access a shared account")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation accepted successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid token or already accepted"),
            @ApiResponse(responseCode = "404", description = "Invitation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Object> accept(@RequestAttribute("userId") UUID userId, @RequestAttribute("userEmail") String userEmail, @PathVariable @Parameter(description = "Invitation token") String token) {
        return ResponseEntity.ok(invitationsService.accept(userId, userEmail, token));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an invitation", description = "Deletes or revokes an invitation")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Invitation deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Invitation not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Invitation ID") UUID id) {
        invitationsService.remove(userId, id);
        return ResponseEntity.noContent().build();
    }
}
