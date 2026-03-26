package com.pos.pos_inventory.controller;

import com.pos.pos_inventory.dto.InventoryRequest;
import com.pos.pos_inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private InventoryService inventoryService;

    @PostMapping("")
    public ResponseEntity<com.pos.user.util.ApiResponse<?>> createProduct(@RequestBody InventoryRequest request){

        inventoryService.createProduct;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.pos.user.util.ApiResponse.success("product added sucssefully", null));
    }
}
