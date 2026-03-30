package com.pos.pos_product.controller;

import com.pos.pos_product.service.ProductService;
import com.pos.pos_product.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/remote")
@RequiredArgsConstructor
public class RemoteController {
    private final ProductService productService;

    @GetMapping("/product/exists/{id}")
    public ResponseEntity<ApiResponse<Boolean>> existsProduct(
            @PathVariable Integer id
    ) {
        boolean exists = productService.existsProduct(id);
        return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                        .status(HttpStatus.OK.value())
                        .message("Product existence checked")
                        .data(exists)
                        .build());
    }
}
