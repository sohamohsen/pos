package com.pos.pos_inventory.client;

import com.pos.pos_inventory.exception.ResourceNotFoundException;
import com.pos.pos_inventory.exception.ServiceUnavailableException;
import com.pos.pos_inventory.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClientService {

    private final ProductClient productClient;

    public void validateProductExists(Integer productId) {
        try {
            ApiResponse<Boolean> response = productClient.existsProduct(productId);

            if (response == null || response.getData() == null || !response.getData()) {
                throw new ResourceNotFoundException(
                        "Product not found with id: " + productId
                );
            }

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Product service call failed for productId={}", productId, e);
            throw new ServiceUnavailableException(
                    "Product service is currently unavailable. Please try again later."
            );
        }
    }
}