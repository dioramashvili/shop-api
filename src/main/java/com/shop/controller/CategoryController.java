package com.shop.controller;

import com.shop.dto.request.CategoryRequest;
import com.shop.dto.response.CategoryResponse;
import com.shop.exception.ErrorResponse;
import com.shop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new category", description = "Creates a category with a unique name")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Returns all categories with their products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by ID", description = "Returns a single category with its products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category by ID", description = "Updates category name and description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Category name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID", description = "Deletes a category and its associated products")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", required = true, example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
