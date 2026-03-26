package com.pos.user.entity;

import com.pos.user.entity.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_logs")
public class AuditLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private Integer performedBy;   // user id who performed the action

    @Column(nullable = false)
    private Integer targetUser;    // affected user id

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String ipAddress;

    @Column(length = 1000)
    private String details;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

}
