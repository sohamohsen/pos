package com.pos.pos_payment.service;

import com.pos.pos_payment.client.InventoryClientService;
import com.pos.pos_payment.client.ProductClientService;
import com.pos.pos_payment.client.CustomerClient;
import com.pos.pos_payment.dto.*;
import com.pos.pos_payment.model.Payment;
import com.pos.pos_payment.model.PaymentItem;
import com.pos.pos_payment.model.Receipt;
import com.pos.pos_payment.exception.ResourceNotFoundException;
import com.pos.pos_payment.model.enums.PaymentStatus;
import com.pos.pos_payment.repository.PaymentRepository;
import com.pos.pos_payment.repository.ReceiptRepository;
import com.pos.pos_payment.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final ProductClientService productClientService;
    private final InventoryClientService inventoryClientService;
    private final CustomerClient customerClient;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {

        Integer cashierId = getCurrentUserId();

        // 1. Validate all products exist and deduct stock
        for (PaymentItemRequest itemReq : request.getItems()) {
            productClientService.validateProductExists(itemReq.getProductId());
        }

        // 2. Deduct stock for each item (after all products validated)
        for (PaymentItemRequest itemReq : request.getItems()) {
            DeductStockRequest deductRequest = new DeductStockRequest();
            deductRequest.setQuantity(itemReq.getQuantity());
            deductRequest.setPerformedBy(cashierId);
            inventoryClientService.deductStock(itemReq.getInventoryId(), deductRequest);
        }

        // 3. Build Payment entity
        BigDecimal totalAmount = request.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = Payment.builder()
                .branchId(request.getBranchId())
                .cashierId(cashierId)
                .customerId(request.getCustomerId())
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.COMPLETED)
                .build();

        // 4. Build PaymentItem entities
        List<PaymentItem> items = request.getItems().stream()
                .map(itemReq -> {
                    BigDecimal lineTotal = itemReq.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                    return PaymentItem.builder()
                            .payment(payment)
                            .productId(itemReq.getProductId())
                            .inventoryId(itemReq.getInventoryId())
                            .quantity(itemReq.getQuantity())
                            .unitPrice(itemReq.getUnitPrice())
                            .totalPrice(lineTotal)
                            .build();
                })
                .toList();

        payment.setItems(items);

        // 5. Generate Receipt
        String receiptNumber = generateReceiptNumber();
        Receipt receipt = Receipt.builder()
                .payment(payment)
                .receiptNumber(receiptNumber)
                .build();

        payment.setReceipt(receipt);

        Payment saved = paymentRepository.save(payment);

        // 6. Award loyalty points if customer is linked
        if (request.getCustomerId() != null) {
            BigDecimal pointsToAward = totalAmount.divide(BigDecimal.TEN, 2, java.math.RoundingMode.HALF_UP);
            try {
                customerClient.addPoints(request.getCustomerId(), pointsToAward);
            } catch (Exception e) {
                // Log and swallow error to avoid failing the payment due to point award error
                // In a production system, this could be put onto a message queue instead.
            }
        }

        return mapToPaymentResponse(saved, receiptNumber);
    }

    public PaymentResponse getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        String receiptNumber = payment.getReceipt() != null
                ? payment.getReceipt().getReceiptNumber()
                : null;
        return mapToPaymentResponse(payment, receiptNumber);
    }

    public PageResponse<PaymentResponse> searchPayments(
            Integer branchId, Integer cashierId, int page, int size) {

        if (page < 0) throw new IllegalArgumentException("Page index must not be negative");
        if (size <= 0 || size > 100) throw new IllegalArgumentException("Page size must be between 1 and 100");

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Payment> resultPage = paymentRepository.findPayments(branchId, cashierId, pageable);

        List<PaymentResponse> content = resultPage.getContent().stream()
                .map(p -> mapToPaymentResponse(p,
                        p.getReceipt() != null ? p.getReceipt().getReceiptNumber() : null))
                .toList();

        return PageResponse.<PaymentResponse>builder()
                .content(content)
                .pageNumber(resultPage.getNumber())
                .pageSize(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .last(resultPage.isLast())
                .build();
    }

    // =============== Helpers ===============

    private String generateReceiptNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String shortUuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "RCP-" + date + "-" + shortUuid;
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Integer) auth.getPrincipal();
    }

    private PaymentResponse mapToPaymentResponse(Payment payment, String receiptNumber) {
        List<PaymentItemResponse> itemResponses = payment.getItems().stream()
                .map(item -> PaymentItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .inventoryId(item.getInventoryId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .toList();

        return PaymentResponse.builder()
                .id(payment.getId())
                .branchId(payment.getBranchId())
                .cashierId(payment.getCashierId())
                .customerId(payment.getCustomerId())
                .totalAmount(payment.getTotalAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .items(itemResponses)
                .receiptNumber(receiptNumber)
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
