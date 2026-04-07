package com.pos.pos_customer.controller;

import com.pos.pos_customer.dto.CustomerDto;
import com.pos.pos_customer.service.CustomerService;
import com.pos.pos_customer.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CustomerDto>> createCustomer(
            @Valid @RequestBody CustomerDto request) {
        CustomerDto response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CustomerDto>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success("Customer fetched", customerService.getCustomerById(id)));
    }

    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CustomerDto>> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(ApiResponse.success("Customer fetched", customerService.getCustomerByPhone(phone)));
    }

    @PostMapping("/{id}/points")
    @PreAuthorize("hasAnyRole('CASHIER','BRANCH_MANAGER','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CustomerDto>> addPoints(
            @PathVariable Integer id,
            @RequestParam BigDecimal points) {
        return ResponseEntity.ok(ApiResponse.success("Points updated", customerService.addPoints(id, points)));
    }
}
