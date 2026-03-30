package com.pos.pos_inventory.client;

import com.pos.pos_inventory.exception.ResourceNotFoundException;
import feign.Logger;
import feign.Request;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.ServiceUnavailableException;
import java.util.concurrent.TimeUnit;

@Configuration
public class FeignClientConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // logs method, URL, status, time
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
                2, TimeUnit.SECONDS,   // connect timeout
                5, TimeUnit.SECONDS,   // read timeout
                true
        );
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> switch (response.status()) {
            case 400 -> new BadRequestException("Invalid request to product service");
            case 404 -> new ResourceNotFoundException("Product not found");
            case 503 -> new ServiceUnavailableException("Product service is unavailable");
            default  -> new RuntimeException("Product service error: " + response.status());
        };
    }
}