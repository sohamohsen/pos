package com.pos.user.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal {

    private Integer userId;
    private String username;
    private String role;
    private Integer branchId;
}