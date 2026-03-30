package com.pos.pos_inventory.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InitRequest {
    private Integer productId;
    private Integer branchId;
    private Integer availableQuantity;
    private Integer displayQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;

}
