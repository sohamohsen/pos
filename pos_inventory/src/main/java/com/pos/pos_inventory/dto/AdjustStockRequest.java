package com.pos.pos_inventory.dto;

import com.pos.pos_inventory.model.enums.MovementReason;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustStockRequest {

    private Integer quantity;

    private MovementReason reason;

    private String note;
    private Integer performedBy;
}