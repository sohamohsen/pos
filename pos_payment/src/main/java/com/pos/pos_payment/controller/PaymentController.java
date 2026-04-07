package com.pos.pos_payment.controller;

import com.pos.pos_payment.dto.PaymentRequest;
import com.pos.pos_payment.dto.PaymentResponse;
import com.pos.pos_payment.service.PaymentService;
import com.pos.pos_payment.util.ApiResponse;
import com.pos.pos_payment.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /payments
     * Process a new sale – deducts stock, records payment, generates receipt.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", response));
    }

    /**
     * GET /payments/{id}
     * Retrieve a single payment by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Integer id) {

        PaymentResponse response = paymentService.getPaymentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Payment fetched successfully", response));
    }

    /**
     * GET /payments/search?branchId=&cashierId=&page=&size=
     * Paginated search with optional filters.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> searchPayments(
            @RequestParam(required = false) Integer branchId,
            @RequestParam(required = false) Integer cashierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<PaymentResponse> response =
                paymentService.searchPayments(branchId, cashierId, page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Payments fetched successfully", response));
    }
}
