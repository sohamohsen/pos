package com.pos.pos_product.model;

import com.pos.pos_product.model.enums.AuditAction;
import com.pos.pos_product.model.enums.AuditField;
import com.pos.pos_product.model.enums.AuditTable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditField fieldName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditTable tableName;

    private String oldValue;
    private String newValue;

    private Integer changedBy;

    private LocalDateTime changedAt = LocalDateTime.now();
}