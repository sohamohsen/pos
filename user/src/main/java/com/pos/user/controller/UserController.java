package com.pos.user.controller;

import com.pos.user.dto.ChangePasswordRequest;
import com.pos.user.util.ApiResponse;
import com.pos.user.service.UserService;
import com.pos.user.util.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<?>> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal CustomUserPrincipal principal){

        userService.changePassword(principal, httpRequest, request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null));
    }
}
