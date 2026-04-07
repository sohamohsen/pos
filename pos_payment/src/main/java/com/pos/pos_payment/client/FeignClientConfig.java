package com.pos.pos_payment.client;

import com.pos.pos_payment.exception.InsufficientStockException;
import com.pos.pos_payment.exception.ResourceNotFoundException;
import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                2, TimeUnit.SECONDS,
                5, TimeUnit.SECONDS,
                true
        );
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> switch (response.status()) {
            case 404 -> new ResourceNotFoundException("Resource not found in downstream service");
            case 409 -> new InsufficientStockException("Insufficient stock for requested quantity");
            default  -> new RuntimeException("Downstream service error: " + response.status());
        };
    }
}
