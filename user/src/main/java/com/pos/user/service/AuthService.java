package com.pos.user.service;

import com.pos.user.dto.*;
import com.pos.user.entity.User;
import com.pos.user.entity.enums.ActionType;
import com.pos.user.entity.enums.Role;
import com.pos.user.exception.ResourceNotFoundException;
import com.pos.user.repository.UserRepository;
import com.pos.user.security.JwtService;
import org.springframework.security.access.AccessDeniedException;
import com.pos.user.util.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public RegistrationResponse registration(
            CustomUserPrincipal principal,
            RegistrationRequest request,
            HttpServletRequest httpRequest) {

        User creator = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role role = Role.valueOf(request.getRole().toUpperCase());

        // Branch restriction
        if (!principal.getRole().equals("ROLE_SUPER_ADMIN")) {

            if (!principal.getBranchId().equals(request)) {
                throw new AccessDeniedException("Cannot create user in another branch");
            }
        }

        String rawPassword = generatePassword(request.getName());

        User user = new User();
        user.setName(request.getName());
        user.setRole(role);
        user.setUsername(generateUsername(request.getName()));
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setBranchId(request.getBranchId());
        user.setIsActive(false);

        User savedUser = userRepository.save(user);

        auditService.log(
                mapToAuditRequest(ActionType.USER_CREATED,
                        creator.getId(),
                        savedUser.getId(),
                        "User created"),
                httpRequest
        );

        return RegistrationResponse.builder()
                .username(savedUser.getUsername())
                .password(rawPassword)
                .role(savedUser.getRole().name())
                .build();
    }

    public LoginResponse login(
            HttpServletRequest httpRequest,
            LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            auditService.log(
                    mapToAuditRequest(
                            ActionType.USER_LOGIN_FAILED,
                            null,
                            null,
                            "Invalid password attempt"
                    ),
                    httpRequest
            );

            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(user);

        auditService.log(
                mapToAuditRequest(
                        ActionType.USER_LOGIN_SUCCESS,
                        user.getId(),
                        user.getId(),
                        "User logged in successfully"
                ),
                httpRequest
        );

        return LoginResponse.builder()
                .token(token)
                .mustChangePassword(!user.getIsActive())
                .build();
    }

    private String generateUsername(String name) {
        return name.toLowerCase().replaceAll("\\s+", "")
                + UUID.randomUUID().toString().substring(0,5);
    }

    private String generatePassword(String name) {
        return name + new SecureRandom().nextInt(999);
    }

    private AuditRequest mapToAuditRequest(
            ActionType actionType,
            Integer performedBy,
            Integer targetUser,
            String details){

        return AuditRequest.builder()
                .actionType(actionType)
                .performedBy(performedBy)
                .targetUser(targetUser)
                .details(details)
                .build();
    }
}