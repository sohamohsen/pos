package com.pos.user.service;

import com.pos.user.dto.AuditRequest;
import com.pos.user.dto.ChangePasswordRequest;
import com.pos.user.entity.enums.ActionType;
import com.pos.user.entity.User;
import com.pos.user.exception.InvalidPasswordException;
import com.pos.user.exception.ResourceNotFoundException;
import com.pos.user.repository.UserRepository;
import com.pos.user.util.CustomUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public void changePassword(
            CustomUserPrincipal principal,
            HttpServletRequest httpRequest,
            ChangePasswordRequest request){

        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1️⃣ تحقق إن الباسورد الجديد مش نفس القديم
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            auditService.log(
                    mapToAuditRequest(
                            ActionType.PASSWORD_CHANGED,
                            user.getId(),
                            user.getId(),
                            "Attempted to reuse old password"
                    ),
                    httpRequest
            );

            throw new InvalidPasswordException("New password must be different from old password");
        }

        // 2️⃣ شفر واحفظ
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3️⃣ أول مرة يغير الباسورد → يفعل الحساب
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            user.setIsActive(true);
        }

        userRepository.save(user);

        auditService.log(
                mapToAuditRequest(
                        ActionType.PASSWORD_CHANGED,
                        user.getId(),
                        user.getId(),
                        "Password changed successfully"
                ),
                httpRequest
        );
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