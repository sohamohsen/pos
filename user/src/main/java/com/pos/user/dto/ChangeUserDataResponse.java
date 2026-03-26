package com.pos.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeUserDataResponse {
    private String username;
    private String code;
    private String role;
    private String password;
}
