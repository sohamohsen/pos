package com.pos.user.dto;

import com.pos.user.entity.enums.BranchType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BranchResponse {
    private Integer id;
    private String name;
    private String code;
    private String location;
    private BranchType type;
    private Boolean isActive;
    private LocalDateTime createdAt;
}