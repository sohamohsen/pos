package com.pos.user.dto;

import lombok.Data;

@Data
public class ChangeUserDataRequest {
    private String code;
    private String role;
}
