package com.pos.pos_inventory.dto;

import com.pos.pos_inventory.model.enums.BranchType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BranchExistResponse {
    private boolean exist;
    private BranchType branchType;
}
