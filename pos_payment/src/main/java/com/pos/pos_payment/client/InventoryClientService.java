package com.pos.pos_payment.client;

import com.pos.pos_payment.dto.DeductStockRequest;
import com.pos.pos_payment.exception.InsufficientStockException;
import com.pos.pos_payment.exception.ResourceNotFoundException;
import com.pos.pos_payment.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryClientService {

    private final InventoryClient inventoryClient;

    public void deductStock(Integer inventoryId, DeductStockRequest request) {
        try {
            inventoryClient.deductStock(inventoryId, request);
        } catch (ResourceNotFoundException | InsufficientStockException e) {
            throw e;
        } catch (Exception e) {
            log.error("Inventory service call failed for inventoryId={}", inventoryId, e);
            throw new ServiceUnavailableException(
                    "Inventory service is currently unavailable. Please try again later.");
        }
    }
}
