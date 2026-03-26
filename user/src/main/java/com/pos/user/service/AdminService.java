package com.pos.user.service;

import com.pos.user.dto.*;
import com.pos.user.entity.User;
import com.pos.user.entity.enums.ActionType;
import com.pos.user.entity.enums.Role;
import com.pos.user.exception.ResourceNotFoundException;
import com.pos.user.repository.UserRepository;
import com.pos.user.repository.UserSpecification;
import com.pos.user.util.CustomUserPrincipal;
import com.pos.user.util.ExcelExportUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final ExcelExportUtil excelExportUtil;
    private final SecureRandom secureRandom = new SecureRandom();

    public ChangeUserDataResponse changePasswordAdmin(
            CustomUserPrincipal principal,
            HttpServletRequest httpRequest,
            ChangeUserDataRequest request) {

        User admin = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        User user = userRepository.findByCode(request.getCode())
                .orElseThrow(() -> new BadCredentialsException("Invalid code"));

        validateBranchAccess(principal, user);

        String rawPassword = generatePassword(user.getRole().name(), user.getUsername());

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setIsActive(false);

        userRepository.save(user);

        auditService.log(
                mapToAuditRequest(ActionType.PASSWORD_CHANGED,
                        admin.getId(),
                        user.getId(),
                        "Admin reset password"),
                httpRequest
        );

        return ChangeUserDataResponse.builder()
                .username(user.getUsername())
                .password(rawPassword)
                .code(user.getCode())
                .build();
    }

    public ChangeUserDataResponse changeUserRole(
            CustomUserPrincipal principal,
            HttpServletRequest httpRequest,
            ChangeUserDataRequest request){

        User admin = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        User user = userRepository.findByCode(request.getCode())
                .orElseThrow(() -> new BadCredentialsException("Invalid code"));

        validateBranchAccess(principal, user);

        Role newRole = Role.valueOf(request.getRole().toUpperCase());

        user.setRole(newRole);

        userRepository.save(user);

        auditService.log(
                mapToAuditRequest(ActionType.ROLE_CHANGED,
                        admin.getId(),
                        user.getId(),
                        "Role changed to " + newRole),
                httpRequest
        );

        return ChangeUserDataResponse.builder()
                .username(user.getUsername())
                .code(user.getCode())
                .build();
    }

    private void validateBranchAccess(CustomUserPrincipal principal, User user) {

        if (!principal.getRole().equals("ROLE_SUPER_ADMIN")) {

            if (principal.getBranchId() == null ||
                    !principal.getBranchId().equals(user.getBranchId())) {

                throw new AccessDeniedException("Cannot manage user from another branch");
            }
        }
    }

    private String generatePassword(String role, String username) {
        int randomNum = secureRandom.nextInt(900) + 100;
        return String.format("%s%d", username, randomNum);
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

    public PaginatedResponseDTO<UserProfileResponse> allUsers(
            int page,
            int size,
            Boolean isActive,
            String code,
            String name,
            String username,
            String role,
            LocalDate start,
            LocalDate end
    ) {

        Specification<User> specification =
                UserSpecification.filterUsers(
                        isActive, code, name, username, role, start, end
                );

        Page<User> usersPage =
                userRepository.findAll(
                        specification,
                        PageRequest.of(page, size)
                );

        Page<UserProfileResponse> responsePage =
                usersPage.map(this::mapToUserProfileResponse);

        return PaginatedResponseDTO.<UserProfileResponse>builder()
                .content(responsePage.getContent())
                .page(responsePage.getNumber())
                .size(responsePage.getSize())
                .totalElements((int) responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();
    }

    public byte[] exportUsers(
            Boolean isActive,
            String code,
            String name,
            String username,
            String role,
            LocalDate start,
            LocalDate end
    ) {

        Specification<User> specification =
                UserSpecification.filterUsers(
                        isActive, code, name, username, role, start, end
                );

        List<UserProfileResponse> data =
                userRepository.findAll(specification)
                        .stream()
                        .map(this::mapToUserProfileResponse)
                        .toList();

        Map<String, List<UserProfileResponse>> sheets = new HashMap<>();
        sheets.put("Users Report", data);

        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("Code", safeValue(code));
        filters.put("Name", safeValue(name));
        filters.put("Username", safeValue(username));
        filters.put("Role", safeValue(role));
        filters.put("Is Active", safeValue(isActive));
        filters.put("Start Date", safeValue(start));
        filters.put("End Date", safeValue(end));

        return excelExportUtil.exportMultiSheet(
                sheets,
                UserProfileResponse.class,
                filters,
                false
        );
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .code(user.getCode())
                .name(user.getName())
                .username(user.getUsername())
                .role(user.getRole().name())
                .notActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String safeValue(Object value) {
        return value == null ? "" : value.toString();
    }
}