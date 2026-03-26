package com.pos.pos_inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PosInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosInventoryApplication.class, args);
    }

}
