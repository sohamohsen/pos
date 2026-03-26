package com.pos.pos_product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private String barcode;
    private Integer categoryId;
    private Boolean active;
    private String imgUrl;
    private LocalDateTime createdAt;
}
