package com.pos.pos_inventory.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InventoryRequest {
    private Integer productId;
    private String locationType;
    private Integer locationId;
    private Integer availableQuantity;
    private Integer displayQuantity;
    private Integer reorderLevel;
}
