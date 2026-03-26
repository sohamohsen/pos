package com.pos.user.controller;

import com.pos.user.dto.ChangeUserDataRequest;
import com.pos.user.dto.ChangeUserDataResponse;
import com.pos.user.dto.PaginatedResponseDTO;
import com.pos.user.dto.UserProfileResponse;
import com.pos.user.service.AdminService;
import com.pos.user.util.ApiResponse;
import com.pos.user.util.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangeUserDataRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal CustomUserPrincipal principal){

        ChangeUserDataResponse response =
                adminService.changePasswordAdmin(principal, httpRequest, request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", response));
    }

    @PatchMapping("/change-role")
    public ResponseEntity<ApiResponse<?>> changeRole(
            @RequestBody ChangeUserDataRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal CustomUserPrincipal principal){

        ChangeUserDataResponse response =
                adminService.changeUserRole(principal, httpRequest, request);

        return ResponseEntity.ok(
                ApiResponse.success("Role changed successfully", response));
    }

    @GetMapping("/all-user")
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate end,
            @RequestParam(defaultValue = "false") boolean export
    ) {

        if (export) {

            byte[] file = adminService.exportUsers(
                    isActive, code, name, username, role, start, end
            );

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=users-report.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(ApiResponse.success("Export successful", file));
        }

        PaginatedResponseDTO<UserProfileResponse> response =
                adminService.allUsers(
                        page, size, isActive, code, name, username, role, start, end
                );

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", response));
    }
}