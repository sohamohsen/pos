package com.pos.pos_inventory.dto;

import lombok.Data;

@Data
public class DeductStockRequest {
    private Integer quantity;

    private String referenceId;

    private Integer performedBy;
}
