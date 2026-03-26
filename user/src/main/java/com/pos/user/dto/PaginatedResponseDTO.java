package com.pos.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDTO<T> {

    private List<T> content;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Integer totalElements;

    private boolean last;
}
