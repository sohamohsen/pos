package com.pos.user.dto;

import com.pos.user.entity.enums.BranchType;
import lombok.Data;

@Data
public class BranchRequest {
    private String name;
    private String code;
    private String location;
    private BranchType type;
}