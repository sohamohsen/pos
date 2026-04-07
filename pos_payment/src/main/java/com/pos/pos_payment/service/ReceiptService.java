package com.pos.pos_payment.service;

import com.pos.pos_payment.dto.PaymentItemResponse;
import com.pos.pos_payment.dto.ReceiptResponse;
import com.pos.pos_payment.exception.ResourceNotFoundException;
import com.pos.pos_payment.model.Payment;
import com.pos.pos_payment.model.Receipt;
import com.pos.pos_payment.repository.ReceiptRepository;
import com.pos.pos_payment.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public ReceiptResponse getByPaymentId(Integer paymentId) {
        Receipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receipt not found for paymentId: " + paymentId));
        return mapToReceiptResponse(receipt);
    }

    public ReceiptResponse getByReceiptNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receipt not found: " + receiptNumber));
        return mapToReceiptResponse(receipt);
    }

    public PageResponse<ReceiptResponse> getAllReceipts(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("Page index must not be negative");
        if (size <= 0 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");

        Pageable pageable = PageRequest.of(page, size, Sort.by("issuedAt").descending());
        Page<Receipt> resultPage = receiptRepository.findAll(pageable);

        List<ReceiptResponse> content = resultPage.getContent().stream()
                .map(this::mapToReceiptResponse)
                .toList();

        return PageResponse.<ReceiptResponse>builder()
                .content(content)
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    private ReceiptResponse mapToReceiptResponse(Receipt receipt) {
        Payment payment = receipt.getPayment();

        List<PaymentItemResponse> items = payment.getItems().stream()
                .map(item -> PaymentItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .inventoryId(item.getInventoryId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return ReceiptResponse.builder()
                .receiptId(receipt.getId())
                .receiptNumber(receipt.getReceiptNumber())
                .paymentId(payment.getId())
                .branchId(payment.getBranchId())
                .cashierId(payment.getCashierId())
                .totalAmount(payment.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .items(items)
                .issuedAt(receipt.getIssuedAt())
                .build();
    }
}
