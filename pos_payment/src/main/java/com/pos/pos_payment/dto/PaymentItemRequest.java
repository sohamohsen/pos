package com.pos.pos_payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentItemRequest {

    @NotNull(message = "productId is required")
    private Integer productId;

    @NotNull(message = "inventoryId is required")
    private Integer inventoryId;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.01", message = "unitPrice must be positive")
    private BigDecimal unitPrice;
}
