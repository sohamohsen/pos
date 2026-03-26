package com.pos.pos_product.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {

    private Integer id;

    private String name;

    private String description;

    private String imageUrl;

    private boolean active;

    private Long activeProudestCount;

    private Long notActiveCategoryCount;

    private LocalDateTime createdAt;
}