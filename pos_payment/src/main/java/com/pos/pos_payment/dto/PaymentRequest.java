package com.pos.pos_payment.dto;

import com.pos.pos_payment.model.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRequest {

    @NotNull(message = "branchId is required")
    private Integer branchId;

    private Integer customerId;

    @NotNull(message = "paymentMethod is required")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<PaymentItemRequest> items;
}
