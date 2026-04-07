package com.pos.pos_product.controller;

import com.pos.pos_product.dto.CategoryResponse;
import com.pos.pos_product.dto.ProductRequest;
import com.pos.pos_product.dto.ProductResponse;
import com.pos.pos_product.dto.UpdateProductRequest;
import com.pos.pos_product.service.ProductService;
import com.pos.pos_product.util.ApiResponse;
import com.pos.pos_product.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createProduct(@RequestBody ProductRequest request){
        productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse
                        .builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Product added successfully.")
                        .data(null)
                        .build()
                );
    }

    @PostMapping("/bulk-upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> bulkUpload(
            @RequestParam("file") MultipartFile file
    ) {

        productService.bulkUploadProducts(file);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Bulk upload completed successfully")
                        .data(null)
                        .build()
        );
    }

    @PatchMapping("product-img/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> productImg(
            @PathVariable Integer id,
            @RequestParam(value = "file") MultipartFile file
    ) {

        productService.addProductImg(id, file);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product Img updated successfully")
                        .data(null)
                        .build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateProduct(
            @PathVariable Integer id,
            @RequestBody UpdateProductRequest request
    ) {

        productService.updateProduct(id, request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product updated successfully")
                        .data(null)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Integer id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product deleted successfully")
                        .data(null)
                        .build());
    }

    @DeleteMapping("product-img/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductImg(
            @PathVariable Integer id,
            @RequestParam(value = "file") MultipartFile file
    ) {

        productService.deleteProductImage(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product Img deleted successfully")
                        .data(null)
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResponse<ProductResponse> response =
                productService.getAllProducts(page, size, name, barcode, categoryId, category, active);

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Products fetched successfully")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Integer id
    ) {

        ProductResponse response =
                productService.getProductById(id);

        return ResponseEntity.ok(
                ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product fetched successfully")
                        .data(response)
                        .build());
    }
}
