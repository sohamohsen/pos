package com.pos.pos_product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String barcode;
    private Integer categoryId;
}
