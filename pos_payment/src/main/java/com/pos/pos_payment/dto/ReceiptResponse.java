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
public class ReceiptResponse {
    private Integer receiptId;
    private String receiptNumber;
    private Integer paymentId;
    private Integer branchId;
    private Integer cashierId;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private List<PaymentItemResponse> items;
    private LocalDateTime issuedAt;
}
