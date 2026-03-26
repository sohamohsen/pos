package com.pos.user.dto;

import com.pos.user.util.ExcelColumn;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {

    @ExcelColumn(name = "Code", order = 1)
    private String code;

    @ExcelColumn(name = "Name", order = 2)
    private String name;

    @ExcelColumn(name = "Username", order = 3)
    private String username;

    @ExcelColumn(name = "Role", order = 4)
    private String role;

    @ExcelColumn(name = "Active", order = 5)
    private Boolean notActive;

    @ExcelColumn(name = "Created At", order = 6)
    private LocalDateTime createdAt;
}