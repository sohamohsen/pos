package com.pos.pos_payment.controller;

import com.pos.pos_payment.dto.ReceiptResponse;
import com.pos.pos_payment.service.ReceiptService;
import com.pos.pos_payment.util.ApiResponse;
import com.pos.pos_payment.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    /**
     * GET /receipts/payment/{paymentId}
     * Fetch the receipt linked to a specific payment.
     */
    @GetMapping("/payment/{paymentId}")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByPaymentId(
            @PathVariable Integer paymentId) {

        ReceiptResponse response = receiptService.getByPaymentId(paymentId);

        return ResponseEntity.ok(
                ApiResponse.success("Receipt fetched successfully", response));
    }

    /**
     * GET /receipts/number/{receiptNumber}
     * Fetch a receipt by its human-readable receipt number (e.g. RCP-20260407-AB12CD34).
     */
    @GetMapping("/number/{receiptNumber}")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByNumber(
            @PathVariable String receiptNumber) {

        ReceiptResponse response = receiptService.getByReceiptNumber(receiptNumber);

        return ResponseEntity.ok(
                ApiResponse.success("Receipt fetched successfully", response));
    }

    /**
     * GET /receipts?page=&size=
     * Paginated list of all receipts (admin/manager only).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ReceiptResponse>>> getAllReceipts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ReceiptResponse> response =
                receiptService.getAllReceipts(page, size);

        return ResponseEntity.ok(
                ApiResponse.success("Receipts fetched successfully", response));
    }
}
