package com.pos.user.controller;

import com.pos.user.dto.LoginRequest;
import com.pos.user.dto.LoginResponse;
import com.pos.user.dto.RegistrationRequest;
import com.pos.user.dto.RegistrationResponse;
import com.pos.user.service.AuthService;
import com.pos.user.util.ApiResponse;
import com.pos.user.util.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','BRANCH_MANAGER')")
    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<?>> registration(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody RegistrationRequest request,
            HttpServletRequest httpRequest){

        RegistrationResponse response =
                authService.registration(principal, request, httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
            HttpServletRequest httpRequest,
            @RequestBody LoginRequest request){

        LoginResponse response = authService.login(httpRequest, request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }
}