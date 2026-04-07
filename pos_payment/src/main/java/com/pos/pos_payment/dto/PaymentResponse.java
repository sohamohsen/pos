package com.pos.pos_payment.dto;

import com.pos.pos_payment.model.enums.PaymentMethod;
import com.pos.pos_payment.model.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PaymentResponse {
    private Integer id;
    private Integer branchId;
    private Integer cashierId;
    private Integer customerId;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private List<PaymentItemResponse> items;
    private String receiptNumber;
    private LocalDateTime createdAt;
}
