package com.pos.pos_inventory.dto;

import com.pos.pos_inventory.model.enums.MovementReason;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustRequest {
    private Integer productId;
    private Integer branchId;
    private Integer availableQuantity;
    private Integer displayQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;
    private Integer damagedQuantity = 0;
    private MovementReason reason;                 // ADJUSTMENT_UP / ADJUSTMENT_DOWN / DAMAGE / THEFT
    private String note;                          // optional free text
    private Integer performedBy;                 // admin userId

}
