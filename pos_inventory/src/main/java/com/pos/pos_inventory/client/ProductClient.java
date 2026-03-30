package com.pos.pos_inventory.client;

import com.pos.pos_inventory.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "pos-product",
        url = "${services.product.base-url}",
        configuration = FeignClientConfig.class
)
public interface ProductClient {

    @GetMapping("/product/exists/{id}")
    ApiResponse<Boolean> existsProduct(@PathVariable("id") Integer id);
}