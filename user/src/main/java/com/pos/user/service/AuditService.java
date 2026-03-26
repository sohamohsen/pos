package com.pos.user.service;

import com.pos.user.dto.AuditRequest;
import com.pos.user.entity.AuditLogs;
import com.pos.user.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public void log(AuditRequest request, HttpServletRequest httpRequest){

        String ipAddress = extractClientIp(httpRequest);

        AuditLogs auditLogs = AuditLogs
                .builder()
                .actionType(request.getActionType())
                .performedBy(request.getPerformedBy())
                .targetUser(request.getTargetUser())
                .ipAddress(ipAddress)
                .details(request.getDetails())
                .build();

        auditLogRepository.save(auditLogs);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

}
