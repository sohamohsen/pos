package com.pos.pos_payment.client;

import com.pos.pos_payment.dto.DeductStockRequest;
import com.pos.pos_payment.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "pos-inventory",
        path = "/api/inventory",
        configuration = FeignClientConfig.class
)
public interface InventoryClient {

    @PostMapping("/inventory/deduct/{inventoryId}")
    ApiResponse<?> deductStock(
            @PathVariable("inventoryId") Integer inventoryId,
            @RequestBody DeductStockRequest request
    );
}
