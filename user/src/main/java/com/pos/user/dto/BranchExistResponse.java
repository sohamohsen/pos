package com.pos.user.dto;

import com.pos.user.entity.enums.BranchType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchExistResponse {
    private boolean exist;
    private BranchType branchType;
}
