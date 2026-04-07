package com.pos.pos_payment.client;

import com.pos.pos_payment.security.FeignClientConfig;
import com.pos.pos_payment.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "pos-customer",
        path = "/api/customer",
        configuration = FeignClientConfig.class
)
public interface CustomerClient {

    @PostMapping("/{id}/points")
    ApiResponse<Object> addPoints(@PathVariable("id") Integer id, @RequestParam("points") BigDecimal points);
}
