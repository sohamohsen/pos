package com.pos.user.dto;

import lombok.Data;

@Data
public class RegistrationRequest {

    private String name;

    private String role;

    private Integer branchId;
    private String notes;
}