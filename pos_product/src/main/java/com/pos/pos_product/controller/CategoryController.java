package com.pos.pos_product.controller;

import com.pos.pos_product.dto.CategoryResponse;
import com.pos.pos_product.service.CategoryService;
import com.pos.pos_product.util.ApiResponse;
import com.pos.pos_product.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createCategory(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(defaultValue = "true") boolean active
    ) {

        categoryService.createCategory(name, description, active);

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Category added successfully.")
                        .data(null)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("category-img/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> categoryImg(
            @PathVariable Integer id,
            @RequestParam(value = "file") MultipartFile file
    ) {

        categoryService.addCategoryImage(id, file);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .data(null)
                        .build());
    }

    @PatchMapping("update-category/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleCategory(
            @PathVariable Integer id,
            @RequestParam(value = "active", required = false) Boolean active
    ) {

        categoryService.toggleCategory(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .data(null)
                        .build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateCategory(
            @PathVariable Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "active", required = false) Boolean active
    ) {

        categoryService.updateCategory(id, name, description, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Integer id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category deleted successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("category-img/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryImg(
            @PathVariable Integer id,
            @RequestParam(value = "file") MultipartFile file
    ) {

        categoryService.deleteCategoryImage(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category deleted successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<CategoryResponse> response =
                categoryService.getAllCategories(page, size, name, active);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CategoryResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Categories fetched successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Integer id
    ) {

        CategoryResponse response =
                categoryService.getCategoryById(id);

        return ResponseEntity.ok(
                ApiResponse.<CategoryResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Category fetched successfully")
                        .data(response)
                        .build());
    }

}