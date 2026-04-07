package com.pos.pos_payment.client;

import com.pos.pos_payment.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "pos-product",
        path = "/api/product",
        configuration = FeignClientConfig.class
)
public interface ProductClient {

    @GetMapping("/remote/product/exists/{id}")
    ApiResponse<Boolean> existsProduct(@PathVariable("id") Integer id);
}
