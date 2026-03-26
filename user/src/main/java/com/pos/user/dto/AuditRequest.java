package com.pos.user.dto;

import com.pos.user.entity.enums.ActionType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuditRequest {

    private ActionType actionType;
    private Integer performedBy;
    private Integer targetUser;
    private String details;
}
