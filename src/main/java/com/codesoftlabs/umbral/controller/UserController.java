package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.UpdateUserDto;
import com.codesoftlabs.umbral.entity.User;
import com.codesoftlabs.umbral.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "users", description = "Manage user profile and account settings")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile information for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public User getMe(@RequestAttribute("userId") UUID userId) {
        return userService.findById(userId);
    }

    @PatchMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates the profile information for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public User updateMe(@RequestAttribute("userId") UUID userId, @Valid @RequestBody UpdateUserDto updateDto) {
        return userService.update(userId, updateDto);
    }
}
