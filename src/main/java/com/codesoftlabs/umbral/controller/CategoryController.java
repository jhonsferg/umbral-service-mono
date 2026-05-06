package com.codesoftlabs.umbral.controller;

import com.codesoftlabs.umbral.dto.CreateCategoryDto;
import com.codesoftlabs.umbral.dto.UpdateCategoryDto;
import com.codesoftlabs.umbral.entity.Category;
import com.codesoftlabs.umbral.service.CategoryService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "categories", description = "Manage transaction categories for expense and income categorization")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a new transaction category for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Category create(@RequestAttribute("userId") UUID userId, @Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.create(userId, createCategoryDto);
    }

    @GetMapping
    @Operation(summary = "Get all categories for the current user", description = "Retrieves all categories, optionally filtered by transaction type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all categories", content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<Category> findAll(@RequestAttribute("userId") UUID userId, @RequestParam(required = false) @Parameter(description = "Filter by transaction type (EXPENSE or INCOME)") String type) {
        return categoryService.findAll(userId, type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single category by ID", description = "Retrieves a specific category by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Category findOne(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Category ID") UUID id) {
        return categoryService.findOne(userId, id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a category", description = "Updates an existing category with new information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Category update(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Category ID") UUID id, @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        return categoryService.update(userId, id, updateCategoryDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category", description = "Deletes an existing category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public void remove(@RequestAttribute("userId") UUID userId, @PathVariable @Parameter(description = "Category ID") UUID id) {
        categoryService.remove(userId, id);
    }
}
