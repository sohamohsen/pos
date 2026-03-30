package com.pos.pos_inventory.dto;

import com.pos.pos_inventory.model.enums.BranchType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitResponse {
    private Integer id;
    private Integer productId;
    private BranchType locationType;
    private Integer locationId;
    private Integer availableQuantity;
    private Integer displayQuantity;
    private Integer reorderLevel;
    private Integer reorderQuantity;
}
