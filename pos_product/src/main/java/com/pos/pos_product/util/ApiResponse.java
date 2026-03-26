package com.pos.pos_product.util;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiResponse<T> {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private boolean success;
    private String message;
    private T data;
    private String path;
}
