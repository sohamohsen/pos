package com.pos.pos_inventory.controller;

import com.pos.pos_inventory.dto.InitRequest;
import com.pos.pos_inventory.dto.InitResponse;
import com.pos.pos_inventory.service.InventoryService;
import com.pos.pos_inventory.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.ServiceUnavailableException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<?>> createInitProduct(@RequestBody InitRequest request) throws ServiceUnavailableException {

        InitResponse response = inventoryService.createInit (request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("product added sucssefully", null));
    }
}
