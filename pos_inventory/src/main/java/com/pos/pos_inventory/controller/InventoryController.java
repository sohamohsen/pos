package com.pos.pos_inventory.controller;

import com.pos.pos_inventory.dto.DeductStockRequest;
import com.pos.pos_inventory.dto.InitRequest;
import com.pos.pos_inventory.dto.InitResponse;
import com.pos.pos_inventory.service.InventoryService;
import com.pos.pos_inventory.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<InitResponse>> createInitProduct(
            @RequestBody InitRequest request) {

        InitResponse response = inventoryService.createInit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory initialized successfully", response));
    }

    @PostMapping("/deduct/{inventoryId}")
    public ResponseEntity<ApiResponse<?>> deductStock(
            @PathVariable Integer inventoryId,
            @RequestBody DeductStockRequest request) {

        inventoryService.deductStock(inventoryId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Stock deducted successfully"));
    }
}
