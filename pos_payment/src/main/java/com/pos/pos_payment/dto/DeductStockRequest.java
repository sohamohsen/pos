package com.pos.pos_payment.dto;

import lombok.Data;

@Data
public class DeductStockRequest {
    private Integer quantity;
    private String referenceId;
    private Integer performedBy;
}
