package com.pos.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponse {
    private String name;
    private String role;
    private String username;
    private String password;
    private String code;
}
